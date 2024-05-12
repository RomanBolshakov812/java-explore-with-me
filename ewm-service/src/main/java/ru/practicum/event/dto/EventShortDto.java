package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

@Getter
@Setter
@AllArgsConstructor
public class EventShortDto implements Comparable<EventShortDto> {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    private String eventDate;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;

    @Override
    public int compareTo(EventShortDto otherEventShortDto) {
        return (int) (otherEventShortDto.getViews() - this.getViews());
    }
}
