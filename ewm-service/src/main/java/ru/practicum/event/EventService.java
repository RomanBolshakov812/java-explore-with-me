package ru.practicum.event;

import ru.practicum.event.dto.*;
import ru.practicum.event.specification.EventFilter;

import java.util.List;

public interface EventService {
    EventFullDto addEvent(NewEventDto newEventDto, Long userId);

    List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size);

    EventFullDto getEventByCurrentUser(Long userId, Long eventId);

    EventFullDto updateEventByCurrentUser(UpdateEventUserRequest updateEventDto,
                                          Long userId, Long eventId);

    EventFullDto updateEventByAdmin(UpdateEventAdminRequest updateEventDto, Long eventId);

    List<EventFullDto> searchEventsByAdmin(EventFilter filter, Integer from, Integer size);
}
