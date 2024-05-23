package ru.practicum.event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.category.CategoryRepository;
import ru.practicum.category.model.Category;
import ru.practicum.client.StatsClient;
import ru.practicum.comment.CommentRepository;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.manner.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.error.exception.IncorrectRequestParametersException;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.model.StateAction;
import ru.practicum.event.specification.EventFilter;
import ru.practicum.event.specification.EventSpecification;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;
import ru.practicum.util.CommentListMaker;
import ru.practicum.util.PageMaker;
import ru.practicum.util.ViewGetter;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final StatsClient statsClient;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private static  final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventSpecification eventSpecification;

    @Override
    public EventFullDto addEvent(NewEventDto newEventDto, Long userId) {
        Long catId = newEventDto.getCategory();
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new EntityNotFoundException("Category with id=" + catId  + " was not found"));
        User initiator = getUserIfExist(userId);
        Event event = EventMapper.toEvent(newEventDto, category, initiator);
        isValidTimestamp(event.getEventDate(), 2);
        eventRepository.save(event);
        return EventMapper.toEventFullDto(event);
    }

    // Получение событий, добавленных текущим пользователем
    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByUser(Long initiatorId, Integer from, Integer size) {
        Pageable page = PageMaker.toPage(from, size);
        getUserIfExist(initiatorId);
        List<Event> events = eventRepository.findEventsByInitiatorId(initiatorId, page).toList();
        HashMap<Long, Long> views = ViewGetter.getViews(statsClient, events,
                null, null);
        List<Comment> allComments = getComments(events);
        HashMap<Long, Long> commentsCountByEvent = CommentListMaker.getCommentsCountByEvent(events,
                allComments);
        return EventMapper.toEventShortDtoList(events, views, commentsCountByEvent);
    }

    // Получение полной информации о событии, добавленной текущим пользователем
    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventByCurrentUser(Long userId, Long eventId) {
        Event event = getEventIfExist(eventId);
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new IncorrectRequestParametersException("User with id=" + userId
                    + " no added events with id=" + eventId + "!");
        }
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        eventFullDto.setViews(ViewGetter.getViews(statsClient, List.of(event), null, null)
                .get(eventId));
        List<CommentDto> comments = getCommentsByCurrentEvent(eventId);
        eventFullDto.setCommentsCount((long) comments.size());
        eventFullDto.setComments(comments);
        return eventFullDto;
    }

    // Изменение события, добавленного текущим пользователем
    @Override
    public EventFullDto updateEventByCurrentUser(
            UpdateEventUserRequest updateEventUserRequest,
            Long userId,
            Long eventId) {
        isValid(updateEventUserRequest);
        Event currentEvent = getEventIfExist(eventId);
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
                currentEvent.setState(State.CANCELED);
            } else {
                currentEvent.setState(State.PENDING);
            }
        }
        return generalUpdateEvent(currentEvent, updateEventUserRequest);
    }

    // Редактирование данных события и его статуса (отклонение/публикация)
    @Override
    public EventFullDto updateEventByAdmin(UpdateEventAdminRequest updateEventAdminRequest,
                                           Long eventId) {
        Event currentEvent = getEventIfExist(eventId);
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
        // Если событие отклоняется - проверка, что оно не PUBLISHED
        // Если публикуется - присвоение PUBLISHED
        EventFullDto eventFullDto;
        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction().equals(StateAction.REJECT_EVENT.name())) {
                if (currentEvent.getState().equals(State.PUBLISHED)) {
                    throw new ValidationException("Only pending and cancelled events "
                            + "can be changed");
                } else {
                    currentEvent.setState(State.CANCELED);
                    eventFullDto = generalUpdateEvent(currentEvent, updateEventAdminRequest);
                }
            } else {
                currentEvent.setState(State.PUBLISHED);
                currentEvent.setPublishedOn(LocalDateTime.now());
                eventFullDto = generalUpdateEvent(currentEvent, updateEventAdminRequest);
            }
        } else {
            eventFullDto = generalUpdateEvent(currentEvent, updateEventAdminRequest);
        }
        return eventFullDto;
    }

    // Поиск событий
    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> searchEventsByAdmin(EventFilter filter, Integer from, Integer size) {
        Pageable page = PageMaker.toPage(from, size);
        Specification<Event> specification = eventSpecification.build(filter);
        List<Event> eventList = eventRepository.findAll(specification, page).toList();
        HashMap<Long, Long> views = ViewGetter.getViews(statsClient, eventList,
                filter.getRangeStart(), filter.getRangeEnd());
        List<Comment> allComments = getComments(eventList);
        HashMap<Long, List<Comment>> commentsByEvent = CommentListMaker
                .getCommentsListByEvent(eventList, allComments);
        HashMap<Long, Long> commentsCountByEvent = CommentListMaker
                .getCommentsCountByEvent(eventList, allComments);
        return EventMapper.toEventFulltDtoList(eventList, views, commentsByEvent,
                commentsCountByEvent);
    }

    // Получение событий с возможностью фильтрации
    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> publicSearchEventsByFilter(
            EventFilter filter,
            HttpServletRequest request,
            String sortParam,
            Integer from,
            Integer size) {
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
        HashMap<Long, Long> views = ViewGetter.getViews(statsClient, eventList,
                filter.getRangeStart(), filter.getRangeEnd());
        List<Comment> allComments = getComments(eventList);
        HashMap<Long, Long> commentsCountByEvent = CommentListMaker
                .getCommentsCountByEvent(eventList, allComments);
        return EventMapper.toEventShortDtoList(eventList, views, commentsCountByEvent);
    }

    //Получение подробной информации об опубликованном событии по его идентификатору
    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = getEventIfExist(eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new EntityNotFoundException("Event with id=" + eventId  + " was not found");
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
        List<CommentDto> comments = getCommentsByCurrentEvent(eventId);
        eventFullDto.setCommentsCount(((long) comments.size()));
        eventFullDto.setComments(comments);
        return eventFullDto;
    }

    private Event getEventIfExist(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Event with id=" + eventId  + " was not found"));
    }

    private User getUserIfExist(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User with id=" + userId  + " was not found"));
    }

    private void isValidTimestamp(LocalDateTime timestamp, Integer timeLag) {
        if (timestamp.isBefore(LocalDateTime.now().plusHours(timeLag))) {
            throw new ValidationException("The date and time of the event must be no earlier than "
                            + LocalDateTime.now().plusHours(timeLag));
        }
    }

    private EventFullDto generalUpdateEvent(Event currentEvent,
                                            UpdateEventUserRequest updateEventDto) {
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
        EventFullDto eventFullDto = EventMapper.toEventFullDto(currentEvent);
        eventFullDto.setComments(getCommentsByCurrentEvent(currentEvent.getId()));
        return eventFullDto;
    }

    private List<CommentDto> getCommentsByCurrentEvent(Long eventId) {
        return CommentMapper.toListCommentDto(commentRepository.findAllByEventId(eventId));
    }

    private List<Comment> getComments(List<Event> events) {
        List<Long> eventIds = new ArrayList<>();
        for (Event event : events) {
            eventIds.add(event.getId());
        }
        return commentRepository.findAllByEventIdIn(eventIds);
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
    }
}
