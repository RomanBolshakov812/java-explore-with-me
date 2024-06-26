package ru.practicum.comment.controller;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.CommentService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "comments/admin/comments", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminCommentController {
    private final CommentService commentService;

    // Удаление комментария админом
    @DeleteMapping("/{commentId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable("commentId") @NotNull @Positive Long commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }
}
