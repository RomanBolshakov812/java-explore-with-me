package ru.practicum.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;

public class CommentAdder {
    public static HashMap<Long, List<Comment>> addCommentsOfEventDto(List<Event> events,
                                                                     List<Comment> allComments) {
        // Создаем мапу <id ивента, пустой список комментов>
        HashMap<Long, List<Comment>> commentsByEvent = new HashMap<>();
        for (Event event : events) {
            List<Comment> commentsList = new ArrayList<>();
            commentsByEvent.put(event.getId(), commentsList);
        }
        // Берем коммент из списка и вписываем его DTO в список соответствующего ивента
        for (Comment comment : allComments) {
            List<Comment> currentCommentList = commentsByEvent.get(comment.getEvent().getId());
            currentCommentList.add(comment);
            commentsByEvent.put(comment.getEvent().getId(), currentCommentList);
        }
        return commentsByEvent;
    }
}
