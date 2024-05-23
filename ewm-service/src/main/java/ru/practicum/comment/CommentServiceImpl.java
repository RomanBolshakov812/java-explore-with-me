package ru.practicum.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.manner.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.error.exception.IncorrectRequestParametersException;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;
import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    // Добавление комментария
    @Override
    public CommentDto addComment(NewCommentDto newCommentDto, Long authorId, Long eventId) {
        if (commentRepository.findByAuthorIdAndEventId(authorId, eventId) != null) {
            throw new IncorrectRequestParametersException("This comment already exists");
        }
        Event event = getEventIfExist(eventId);
        if (event.getInitiator().getId().equals(authorId)) {
            throw new IncorrectRequestParametersException("Еhe initiator of the event "
                    + "cannot comment on it");
        }
        User author = getUserIfExist(authorId);
        LocalDateTime created = LocalDateTime.now();
        Comment comment = CommentMapper.toComment(newCommentDto, event, author, created);
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    // Обновление комментария
    @Override
    public CommentDto updateComment(NewCommentDto newCommentDto,
                                    Long userId, Long eventId, Long commentId) {
        Comment comment = getCommentIfExist(commentId);
        getUserIfExist(userId);
        getEventIfExist(eventId);
        isCommentByAuthor(comment.getAuthor().getId(), userId);
        comment.setText(newCommentDto.getText());
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    // Удаление комментария админом
    @Override
    public void deleteCommentByAdmin(Long commentId) {
        getCommentIfExist(commentId);
        commentRepository.deleteById(commentId);
    }

    // Удаление комментария автором
    @Override
    public void deleteCommentByCurrentUser(Long userId, Long commentId) {
        Comment comment = getCommentIfExist(commentId);
        isCommentByAuthor(comment.getAuthor().getId(), userId);
        commentRepository.deleteById(commentId);
    }

    // Получение комментария по id
    @Override
    @Transactional(readOnly = true)
    public CommentDto getCommentById(Long commentId) {
        return CommentMapper.toCommentDto(getCommentIfExist(commentId));
    }

    // Получение всех комментариев события по id события
    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByEvent(Long eventId) {
        Event event = getEventIfExist(eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new EntityNotFoundException("Event with id=" + eventId  + " was not found");
        }
        return getCommentsByCurrentEvent(eventId);
    }

    private Event getEventIfExist(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Event with id=" + eventId  + " was not found"));
    }

    private User getUserIfExist(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User with id=" + userId  + " was not found"));
    }

    private Comment getCommentIfExist(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException("Comment with id=" + commentId  + " was not found"));
    }

    private void isCommentByAuthor(Long authorId, Long userId) {
        if (!Objects.equals(authorId, userId)) {
            throw new ValidationException("User c id=" + userId + " not the author of the comment");
        }
    }

    private List<CommentDto> getCommentsByCurrentEvent(Long eventId) {
        return CommentMapper.toListCommentDto(commentRepository.findAllByEventId(eventId));
    }
}
