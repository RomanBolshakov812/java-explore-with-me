package ru.practicum.event;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.event.dto.*;
import ru.practicum.event.specification.EventFilter;

public interface EventService {
    EventFullDto addEvent(NewEventDto newEventDto, Long userId);

    List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size);


    EventFullDto getEventByCurrentUser(Long userId, Long eventId);

    EventFullDto updateEventByCurrentUser(UpdateEventUserRequest updateEventDto,
                                          Long userId, Long eventId);

    EventFullDto updateEventByAdmin(UpdateEventAdminRequest updateEventDto, Long eventId);

    List<EventFullDto> searchEventsByAdmin(EventFilter filter, Integer from, Integer size);

    List<EventShortDto> publicSearchEventsByFilter(EventFilter filter, HttpServletRequest request,
                                                   String sort, Integer from, Integer size);

    EventFullDto getEventById(Long id, HttpServletRequest request);}
