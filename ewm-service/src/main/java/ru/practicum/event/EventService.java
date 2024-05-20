package ru.practicum.event;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dto.*;
import ru.practicum.event.specification.EventFilter;

@Transactional
public interface EventService {
    EventFullDto addEvent(NewEventDto newEventDto, Long userId);

    @Transactional(readOnly = true)
    List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size);


    @Transactional(readOnly = true)
    EventFullDto getEventByCurrentUser(Long userId, Long eventId);

    EventFullDto updateEventByCurrentUser(UpdateEventUserRequest updateEventDto,
                                          Long userId, Long eventId);

    EventFullDto updateEventByAdmin(UpdateEventAdminRequest updateEventDto, Long eventId);

    @Transactional(readOnly = true)
    List<EventFullDto> searchEventsByAdmin(EventFilter filter, Integer from, Integer size);

    @Transactional(readOnly = true)
    List<EventShortDto> publicSearchEventsByFilter(EventFilter filter, HttpServletRequest request,
                                                   String sort, Integer from, Integer size);

    @Transactional(readOnly = true)
    EventFullDto getEventById(Long id, HttpServletRequest request);
}
