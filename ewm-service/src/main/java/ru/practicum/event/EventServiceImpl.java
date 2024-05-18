package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.EndpointHit;///////////////////////////////////////////////////
import ru.practicum.ViewStats;
import ru.practicum.category.CategoryRepository;
import ru.practicum.category.model.Category;
import ru.practicum.client.StatsClient;///////////////////////////////////////////////////
import ru.practicum.event.dto.*;
import ru.practicum.event.model.State;
import ru.practicum.event.model.StateAction;
import ru.practicum.event.specification.EventFilter;
import ru.practicum.event.specification.EventSpecification;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;
import ru.practicum.error.exception.IncorrectRequestParametersException;
import ru.practicum.error.exception.IncorrectEntityParametersException;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.util.PageMaker;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    @Autowired
    private final StatsClient statsClient;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private static  final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");////////////////////////////////
    private final EventSpecification eventSpecification;

    @Override
    public EventFullDto addEvent(NewEventDto newEventDto, Long userId) {
        Long catId = newEventDto.getCategory();
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new EntityNotFoundException("Category with id=" + catId  + " was not found"));
        User initiator = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User with id=" + userId + " was not found"));
        Event event = EventMapper.toEvent(newEventDto, category, initiator);
        isValidTimestamp(event.getEventDate(), 2);
        eventRepository.save(event);
        return EventMapper.toEventFullDto(event);
    }

    // Получение событий, добавленных текущим пользователем
    @Override
    public List<EventShortDto> getEventsByUser(Long initiatorId, Integer from, Integer size) {
        Pageable page = PageMaker.toPage(from, size);
        userRepository.findById(initiatorId).orElseThrow(() ->
                new EntityNotFoundException("User with id=" + initiatorId + " was not found"));
        List<Event> events = eventRepository.findEventsByInitiatorId(initiatorId, page).toList();
        HashMap<Long, Long> views = getViews(events,
                null, null);
        return EventMapper.toEventShortDtoList(events, views);
    }

    // Получение полной информации о событии, добавленной текущим пользователем
    @Override
    public EventFullDto getEventByCurrentUser(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Event with id=" + eventId  + " was not found"));
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            return EventMapper.toEventFullDto(event);
        } else {
            throw new IncorrectRequestParametersException("User with id=" + userId
                    + " no added events with id=" + eventId + "!");
        }
    }

    // Изменение события, добавленного текущим пользователем
    @Override
    public EventFullDto updateEventByCurrentUser(
            UpdateEventUserRequest updateEventUserRequest,//////////////////////////////////////////////////////////////////
            Long userId,
            Long eventId) {
        isValid(updateEventUserRequest);
        Event currentEvent = eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Event with id=" + eventId  + " was not found"));
        // Проверка, что событие не PUBLISHED
        if (currentEvent.getState().equals(State.PUBLISHED)) {
            throw new IncorrectRequestParametersException("Only pending or canceled "
                    + "events can be changed");
        }
        // Проверка, что до события не менее 2-х часов
        if (updateEventUserRequest.getEventDate() != null) {
            LocalDateTime newEventDate = LocalDateTime
                    .parse(updateEventUserRequest.getEventDate(), DATE_TIME_FORMATTER);
            isValidTimestamp(newEventDate, 2);
            currentEvent.setEventDate(newEventDate);
        }
        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals(StateAction.CANCEL_REVIEW.name())) {
                currentEvent.setState(State.CANCELED);/////////////////////// НЕ ПОНЯТНО ПОЧЕМУ CANCELLED (так требуется в тесте) а не PENDING...
            } else {
                currentEvent.setState(State.PENDING);
            }
        }

//        Event updatedEvent = EventMapper
//                .toEventFromUpdateEventDto(currentEvent, updateEventUserRequest, newCategory);
//        return EventMapper.toEventFullDto(updatedEvent);
        return generalUpdateEvent(currentEvent, updateEventUserRequest, false);////////////////////////// УБРАТЬ ЭТОТ false !!!!!!!!!!!!!!!!!!!!!!!!!
    }

    // Редактирование данных события и его статуса (отклонение/публикация)
    @Override
    public EventFullDto updateEventByAdmin(UpdateEventAdminRequest updateEventAdminRequest,
                                           Long eventId) {
        Event currentEvent = eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Event with id=" + eventId  + " was not found"));
        // Проверка, что событие PENDING
        if (!currentEvent.getState().equals(State.PENDING)) {
            throw new IncorrectRequestParametersException("Only pending events can be changed");
        }
        // Проверка, что до события не менее 1 часа
        if (updateEventAdminRequest.getEventDate() != null) {
            LocalDateTime newEventDate = LocalDateTime
                    .parse(updateEventAdminRequest.getEventDate(), DATE_TIME_FORMATTER);
            isValidTimestamp(newEventDate, 1);
            currentEvent.setEventDate(newEventDate);
        } else {
            LocalDateTime eventDate = currentEvent.getEventDate();
            isValidTimestamp(eventDate, 1);
        }
        // Если событие отклоняется - проверка, что оно не PUBLISHED и присвоение REJECT_EVENT
        // Если публикуется - присвоение PUBLISHED
        EventFullDto eventFullDto = new EventFullDto();
        boolean isAdmin = true;
        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction().equals(StateAction.REJECT_EVENT.name())) {
                if (currentEvent.getState().equals(State.PUBLISHED)) {
                    throw new ValidationException("Only pending and cancelled events "
                            + "can be changed");
                } else {
                    currentEvent.setState(State.CANCELED);//////////????????????????????????????????////////////////////////////////////////////
                    eventFullDto = generalUpdateEvent(currentEvent, updateEventAdminRequest,
                            isAdmin);
                    //eventFullDto.setState(State.PENDING.name());///////////////////////////////////////////////////////
                }
            } else {
                currentEvent.setState(State.PUBLISHED);
                currentEvent.setPublishedOn(LocalDateTime.now());
                eventFullDto = generalUpdateEvent(currentEvent, updateEventAdminRequest, isAdmin);
            }
        } else {
            eventFullDto = generalUpdateEvent(currentEvent, updateEventAdminRequest, isAdmin);
        }
        return eventFullDto;
    }

    // Поиск событий0000000000000000000000000000000000000000000000000000000000000000000000000
    @Override
    public List<EventFullDto> searchEventsByAdmin(EventFilter filter, Integer from, Integer size) {
        Pageable page = PageMaker.toPage(from, size);
        Specification<Event> specification = eventSpecification.build(filter);
        List<Event> eventList = eventRepository.findAll(specification, page).toList();
        HashMap<Long, Long> views = getViews(eventList,
                filter.getRangeStart(), filter.getRangeEnd());
        return EventMapper.toEventFulltDtoList(eventList, views);
    }

    // Получение событий с возможностью фильтрации
    @Override
    public List<EventShortDto> publicSearchEventsByFilter(EventFilter filter, HttpServletRequest request,
                                                          String sortParam, Integer from, Integer size) {
        if (LocalDateTime.parse(filter.getRangeStart(), DATE_TIME_FORMATTER)
                .isAfter(LocalDateTime.parse(filter.getRangeEnd(), DATE_TIME_FORMATTER))) {
            throw new ValidationException("Incorrect dates");
        }
        Pageable page;
        if (sortParam.equals("EVENT_DATE")) {
            Sort sort = Sort.by(Sort.Direction.ASC, "eventDate");
            int startPage = from / size;
            page = PageRequest.of(startPage, size, sort);
        } else {
            page = PageMaker.toPage(from, size);
        }
        Specification<Event> specification = eventSpecification.build(filter);
        List<Event> eventList = eventRepository.findAll(specification, page).toList();
        addHit(request);
        HashMap<Long, Long> views = getViews(eventList,
                filter.getRangeStart(), filter.getRangeEnd());

        return EventMapper.toEventShortDtoList(eventList, views);
    }

    //Получение подробной информации об опубликованном событии по его идентификатору
    @Override // МЕТОД КРИВОЙ !!!!!!!!(есть старая версия в КУСКАХ)!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public EventFullDto getEventById(Long id, HttpServletRequest request) {
        // Видимо нужно было сразу сделать поиск ивента по id, у которого state был бы PUBLISHED,
        // но я так и не разобрался как работать с enum в запросе к базе
        Event event = eventRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Event with id=" + id  + " was not found"));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new EntityNotFoundException("Event with id=" + id  + " was not found");
        }
        addHit(request);
        List<ViewStats> viewStats = statsClient.getStats(
                event.getPublishedOn().format(DATE_TIME_FORMATTER),
                "9999-12-31 23:59:59",
                List.of("/events/" + event.getId()),
                true);
        Long views;
        if (viewStats.size() != 0) {
            views = viewStats.get(0).getHits();
        } else {
            views = 0L;
        }
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        eventFullDto.setViews(views);
        return eventFullDto;
    }

    private void isValidTimestamp(LocalDateTime timestamp, Integer timeLag) {
        if (timestamp.isBefore(LocalDateTime.now().plusHours(timeLag))) {
            throw new ValidationException(
            //throw new IncorrectEntityParametersException(/////////////////////////////////////////////////////////////////
                    "The date and time of the event must be no earlier than "
                            + LocalDateTime.now().plusHours(timeLag));
        }
    }

    private EventFullDto generalUpdateEvent(Event currentEvent,
                                            UpdateEventUserRequest updateEventDto,
                                            boolean isAdmin) {
        Category newCategory;
        // Назначение новой категории при ее наличии в запросе и в базе
        if (updateEventDto.getCategory() != null && !currentEvent.getCategory()
                .getId().equals(updateEventDto.getCategory())) {
            Long catId = updateEventDto.getCategory();
            newCategory = categoryRepository.findById(catId)
                    .orElseThrow(() -> new EntityNotFoundException("Category with id=" + catId
                            + " was not found"));
            currentEvent.setCategory(newCategory);
        }
        if (updateEventDto.getAnnotation() != null) {
            currentEvent.setAnnotation(updateEventDto.getAnnotation());
        }
        if (updateEventDto.getDescription() != null) {
            currentEvent.setDescription(updateEventDto.getDescription());
        }
        if (updateEventDto.getLocation() != null) {
            currentEvent.setLat(updateEventDto.getLocation().getLat());
            currentEvent.setLon(updateEventDto.getLocation().getLon());
        }
        if (updateEventDto.getPaid() != null) {
            currentEvent.setPaid(updateEventDto.getPaid());
        }
        if (updateEventDto.getParticipantLimit() != null) {
            currentEvent.setParticipantLimit(updateEventDto.getParticipantLimit());
        }
        if (updateEventDto.getRequestModeration() != null) {
            currentEvent.setRequestModeration(updateEventDto.getRequestModeration());
        }
        if (updateEventDto.getTitle() != null) {
            currentEvent.setTitle(updateEventDto.getTitle());
        }
        eventRepository.save(currentEvent);
//        if (isAdmin) {
//            currentEvent.setState(State.CANCELED);////////////////////////////////////////////////////////
//        }
//        if (updateEventDto.getStateAction().equals("CANCEL_REVIEW")) {
//            currentEvent.setState(State.CANCELED);////////////////////////////////////////////////////////
//        }
        //currentEvent.setState(State.CANCELED);////////////////////////////////////////////////////////
        return EventMapper.toEventFullDto(currentEvent);
    }

    public HashMap<Long, Long> getViews(List<Event> events, String start, String end) {
        if (start == null) {
            start = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        }
        if (end == null) {
            end = "9999-12-31 23:59:59";////////////////////////////////////////////////////////////
//            end = LocalDateTime.MAX.format(DATE_TIME_FORMATTER);
        }
        List<Long> idsList = new ArrayList<>();// Список айдишников
        for (Event event : events) {
            idsList.add(event.getId());
        }
        List<String> uris = new ArrayList<>();// Список урлов
        for (Long eventId : idsList) {
            uris.add("/events/" + eventId);
        }
        ArrayList<ViewStats> viewStats = statsClient.getStats(start, end, uris, false);

        HashMap<Long, Long> views = new HashMap<>();
        if (viewStats.size() != 0) {
            for (Long eventId : idsList) {
                views.put(eventId, viewStats.get(0).getHits());
                viewStats.remove(0);
            }
        }
        return views;
    }

    private void addHit(HttpServletRequest request) {
        EndpointHit endpointHit = new EndpointHit(
                "ewm-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
        statsClient.addHit(endpointHit);
    }

    private void isValid(UpdateEventUserRequest updateEventUserRequest) {
//        String title = updateEventUserRequest.getTitle();
//        String annotation = updateEventUserRequest.getAnnotation();
//        String description = updateEventUserRequest.getDescription();
        if (updateEventUserRequest.getTitle() != null) {
            if (updateEventUserRequest.getTitle().isBlank()) {
                throw new ValidationException("Event title cannot be empty");
            }
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            if (updateEventUserRequest.getAnnotation().isBlank()) {
                throw new ValidationException("Event annotation cannot be empty");
            }
        }
        if (updateEventUserRequest.getDescription() != null) {
            if (updateEventUserRequest.getDescription().isBlank()) {
                throw new ValidationException("Event description cannot be empty");
            }
        }

//        if (updateEventUserRequest.getParticipantLimit() != null) {
//            if (updateEventUserRequest.getParticipantLimit() < 0) {
//                throw new ValidationException("Passed a negative participant limit value");
//            }
//        }
    }
}
