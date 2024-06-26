package ru.practicum.event.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.manner.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.State;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

public class EventMapper {
    private static  final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static EventFullDto toEventFullDto(Event event) {
        Location location = new Location(event.getLat(), event.getLon());
        String publishedOn = "";
        if (event.getPublishedOn() != null) {
            publishedOn = event.getPublishedOn().format(DATE_TIME_FORMATTER);
        }
        if (event.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
        if (event.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }
        if (event.getPaid() == null) {
            event.setPaid(false);
        }
        return new EventFullDto(
               event.getId(),
               event.getAnnotation(),
               CategoryMapper.toCategoryDto(event.getCategory()),
               event.getConfirmedRequests(),
               event.getEventDate().format(DATE_TIME_FORMATTER),
               UserMapper.toUserShortDto(event.getInitiator()),
               event.getPaid(),
               event.getTitle(),
               0L,
               0L,
               new ArrayList<>(),
               event.getCreated().toString(),
               event.getDescription(),
               location,
               event.getParticipantLimit(),
               publishedOn,
               event.getRequestModeration(),
               event.getState().name()
       );
    }

    public static Event toEvent(NewEventDto newEventDto, Category category, User initiator) {
        LocalDateTime eventDate
                = LocalDateTime.parse(newEventDto.getEventDate(), DATE_TIME_FORMATTER);
        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(category);
        event.setCreated(LocalDateTime.now());
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(eventDate);
        event.setInitiator(initiator);
        event.setLat(newEventDto.getLocation().getLat());
        event.setLon(newEventDto.getLocation().getLon());
        event.setPaid(newEventDto.getPaid());
        if (newEventDto.getParticipantLimit() == null) {
            newEventDto.setParticipantLimit(0);
        } else {
            event.setParticipantLimit(newEventDto.getParticipantLimit());
        }
        event.setConfirmedRequests(0);
        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        } else {
            event.setRequestModeration(newEventDto.getRequestModeration());
        }
        event.setState(State.PENDING);
        event.setTitle(newEventDto.getTitle());
        return event;
    }

    public static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getEventDate().format(DATE_TIME_FORMATTER),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                0L,
                0L
        );
    }

    public static List<EventShortDto> toEventShortDtoList(
            List<Event> events,
            HashMap<Long, Long> views,
            HashMap<Long, Long> commentsCountByEvent) {
        List<EventShortDto> list = new ArrayList<>();
        for (Event event : events) {
            EventShortDto eventShortDto = toEventShortDto(event);
            eventShortDto.setViews(views.get(eventShortDto.getId()));
            Long commentCount = commentsCountByEvent.get(event.getId());
            eventShortDto.setCommentsCount(commentCount);
            list.add(eventShortDto);
        }
        return list;
    }

    public static List<EventFullDto> toEventFulltDtoList(
            List<Event> events,
            HashMap<Long, Long> views,
            HashMap<Long, List<Comment>> commentsByEvent,
            HashMap<Long, Long> commentsCountByEvent) {
        List<EventFullDto> list = new ArrayList<>();
        for (Event event : events) {
            EventFullDto eventFullDto = toEventFullDto(event);
            eventFullDto.setViews(views.get(eventFullDto.getId()));
            eventFullDto.setCommentsCount(commentsCountByEvent.get(event.getId()));
            List<CommentDto> commentDtoList
                    = CommentMapper.toListCommentDto(commentsByEvent.get(event.getId()));
            eventFullDto.setComments(commentDtoList);
            list.add(eventFullDto);
        }
        return list;
    }
}
