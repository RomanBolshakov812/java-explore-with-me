package ru.practicum.comment;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto addComment(NewCommentDto newCommentDto, Long userId, Long eventId);

    CommentDto updateComment(NewCommentDto newCommentDto,
                             Long userId, Long eventId, Long commentId);

    void deleteCommentByAdmin(Long commentId);

    void deleteCommentByCurrentUser(Long userId, Long commentId);

    CommentDto getCommentById(Long commentId);

    List<CommentDto> getCommentsByEvent(Long eventId);
}
