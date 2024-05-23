package ru.practicum.comment;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comment.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEventId(Long eventId);

    List<Comment> findAllByEventIdIn(List<Long> eventIds);

    Comment findByAuthorIdAndEventId(Long authorId, Long eventId);
}
