package ru.practicum.comment.manner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

public class CommentMapper {

    public static Comment toComment(NewCommentDto newCommentDto, Event event, User author,
                                    LocalDateTime created) {
        return new Comment(
                null,
                newCommentDto.getText(),
                event,
                author,
                created
        );
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static List<CommentDto> toListCommentDto(List<Comment> comments) {
        List<CommentDto> commentsDto = new ArrayList<>();
        for (Comment comment : comments) {
            commentsDto.add(toCommentDto(comment));
        }
        return commentsDto;
    }
}
