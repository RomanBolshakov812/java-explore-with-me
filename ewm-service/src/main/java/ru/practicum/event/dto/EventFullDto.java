package ru.practicum.event.dto;

import java.util.List;
import lombok.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.event.model.Location;
import ru.practicum.user.dto.UserShortDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto extends EventShortDto {
    private String createdOn;
    private String description;
    private Location location;
    private Integer participantLimit;
    private String publishedOn;
    private boolean requestModeration;
    private String state;

    public EventFullDto(Long id, String annotation, CategoryDto category, Integer confirmedRequests,
                        String eventDate, UserShortDto initiator, Boolean paid, String title,
                        Long views, List<CommentDto> comments, String createdOn, String description,
                        Location location, Integer participantLimit, String publishedOn,
                        boolean requestModeration, String state) {
        super(id, annotation, category, confirmedRequests, eventDate,
                initiator, paid, title, views, comments);
        this.createdOn = createdOn;
        this.description = description;
        this.location = location;
        this.participantLimit = participantLimit;
        this.publishedOn = publishedOn;
        this.requestModeration = requestModeration;
        this.state = state;
    }
}
