package ru.practicum.comment.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.CommentService;
import ru.practicum.comment.dto.CommentDto;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/comments", produces = MediaType.APPLICATION_JSON_VALUE)
public class PublicCommentController {
    private final CommentService commentService;
    private static  final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Получение комментария по его id
    @GetMapping("/{commentId}")
    @ResponseStatus(code = HttpStatus.OK)
    public CommentDto getCommentsById(
            @PathVariable("commentId") @NonNull Long commentId) {
        return commentService.getCommentById(commentId);
    }

    // Получение всех комментов события по id события
    @GetMapping("/events/{eventId}/comments")
    @ResponseStatus(code = HttpStatus.OK)
    public List<CommentDto> getCommentsByEvent(
            @PathVariable("eventId") @NonNull Long eventId) {
        return commentService.getCommentsByEvent(eventId);
    }
}
