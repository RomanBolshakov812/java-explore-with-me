package ru.practicum.comment.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.CommentService;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/comments/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserCommentController {

    private final CommentService commentService;

    // Добавление комментария
    @PostMapping("/events/{eventId}/comments")
    public CommentDto addComment(@RequestBody @Valid NewCommentDto newCommentDto,
                                 @PathVariable("userId") @NonNull Long userId,
                                 @PathVariable("eventId") @NonNull Long eventId) {
        return commentService.addComment(newCommentDto, userId, eventId);
    }

    // Обновление комментария
    @PatchMapping("/events/{eventId}/comments/{commentId}")
    public CommentDto updateComment(@RequestBody @Valid NewCommentDto newCommentDto,
                                    @PathVariable("userId") @NonNull Long userId,
                                    @PathVariable("eventId") @NonNull Long eventId,
                                    @PathVariable("commentId") @NotNull Long commentId) {
        return commentService.updateComment(newCommentDto, userId, eventId, commentId);
    }

    // Удаление комментария автором
    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable("userId") @NonNull @Positive Long userId,
                              @PathVariable("commentId") @NotNull @Positive Long commentId) {
        commentService.deleteCommentByCurrentUser(userId, commentId);
    }
}
