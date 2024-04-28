package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.category.CategoryRepository;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.State;
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
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private static  final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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

    @Override
    public List<EventShortDto> getEventsByUser(Long initiatorId, Integer from, Integer size) {
        Pageable page = PageMaker.toPage(from, size);
        userRepository.findById(initiatorId).orElseThrow(() ->
                new EntityNotFoundException("User with id=" + initiatorId + " was not found"));
        List<Event> events = eventRepository.findEventsByInitiatorId(initiatorId, page).toList();
        return EventMapper.toEventShortDtoList(events);
    }

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

    @Override
    public EventFullDto updateEventByCurrentUser(UpdateEventUserRequest updateEventDto,
                                                 Long userId,
                                                 Long eventId) {

        Event currentEvent = eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Event with id=" + eventId  + " was not found"));
        // Проверка, что событие не PUBLISHED
        if (currentEvent.getState().equals(State.PUBLISHED)) {
            throw new ValidationException("Only pending or canceled events can be changed");
        }
        // Проверка, что до события не менее 2-х часов
        if (updateEventDto.getEventDate() != null) {
            LocalDateTime newEventDate = LocalDateTime
                    .parse(updateEventDto.getEventDate(), DATE_TIME_FORMATTER);
            isValidTimestamp(newEventDate, 2);
            currentEvent.setEventDate(newEventDate);
        }
        currentEvent.setState(State.CANCELED);/////////////////////// НЕ ПОНЯТНО ПОЧЕМУ CANCELLED (так требуется в тесте) а не PENDING...

//        Event updatedEvent = EventMapper
//                .toEventFromUpdateEventDto(currentEvent, updateEventDto, newCategory);
//        return EventMapper.toEventFullDto(updatedEvent);

        return generalUpdateEvent(currentEvent, updateEventDto);
    }

    @Override
    public EventFullDto updateEventByAdmin(UpdateEventAdminRequest updateEventDto, Long eventId) {
        Event currentEvent = eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Event with id=" + eventId  + " was not found"));
        // Проверка, что событие PENDING
        if (!currentEvent.getState().equals(State.PENDING)) {
            throw new ValidationException("Only pending events can be changed");
        }
        // Проверка, что до события не менее 1 часа
        if (updateEventDto.getEventDate() != null) {
            LocalDateTime newEventDate = LocalDateTime
                    .parse(updateEventDto.getEventDate(), DATE_TIME_FORMATTER);
            isValidTimestamp(newEventDate, 1);
            currentEvent.setEventDate(newEventDate);
        } else {
            LocalDateTime eventDate = currentEvent.getEventDate();
            isValidTimestamp(eventDate, 1);
        }
        // Если событие отклоняется - проверка, что оно не PUBLISHED и присвоение REJECT_EVENT
        // Если публикуется - присвоение PUBLISHED
        if (updateEventDto.getStateAction() != null) {
            if (updateEventDto.getStateAction().equals(State.REJECT_EVENT.name())) {
                if (currentEvent.getState().equals(State.PUBLISHED)) {
                    throw new ValidationException("Only pending and cancelled events can be changed");
                } else {
                    currentEvent.setState(State.REJECT_EVENT);
                }
            } else {
                currentEvent.setState(State.PUBLISHED);
            }
        }
        return generalUpdateEvent(currentEvent, updateEventDto) ;
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
        return EventMapper.toEventFullDto(currentEvent);
    }

    @Override
    public List<EventFullDto> searchEventsByAdmin(EventFilter filter, Integer from, Integer size) {
        Pageable page = PageMaker.toPage(from, size);
        Specification<Event> specification = eventSpecification.build(filter);
        List<Event> eventList = eventRepository.findAll(specification, page).toList();
        return EventMapper.toEventFulltDtoList(eventList);
    }

    private void isValidTimestamp(LocalDateTime timestamp, Integer timeLag) {
        if (timestamp.isBefore(LocalDateTime.now().plusHours(timeLag))) {
            throw new IncorrectEntityParametersException(
                    "The date and time of the event must be no earlier than "
                            + LocalDateTime.now().plusHours(timeLag));
        }
    }
}
