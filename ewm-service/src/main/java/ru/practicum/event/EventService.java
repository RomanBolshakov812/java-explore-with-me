package ru.practicum.event;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import ru.practicum.event.dto_comment.CommentDto;
import ru.practicum.event.dto_comment.NewCommentDto;
import ru.practicum.event.dto_event.*;
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

    EventFullDto getEventById(Long id, HttpServletRequest request);

    CommentDto addComment(NewCommentDto newCommentDto, Long userId, Long eventId);

    CommentDto updateComment(NewCommentDto newCommentDto,
                             Long userId, Long eventId, Long commentId);

    void deleteCommentByAdmin(Long commentId);

    void deleteCommentByCurrentUser(Long userId, Long commentId);

    CommentDto getCommentById(Long commentId);

    List<CommentDto> getCommentsByEvent(Long eventId);
}
