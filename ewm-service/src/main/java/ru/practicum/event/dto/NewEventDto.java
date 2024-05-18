package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.boot.context.properties.bind.DefaultValue;
import ru.practicum.event.model.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@AllArgsConstructor
public class NewEventDto {
//    private Long id;////////////////////////////////////////////////////
    @NotNull
    @NotBlank
    @Length(min = 20, max = 2000)
    private String annotation;
    @NotNull
    private Long category;
    @NotNull
    @Length(min = 20, max = 7000)
    @NotBlank
    private String description;
    @NotNull
    private String eventDate;
    @NotNull
    private Location location;
    private Boolean paid;
    @PositiveOrZero
    private Integer participantLimit;
    private Boolean requestModeration;
    @NotNull
    @NotBlank
    @Length(min = 3, max = 120)
    private String title;
}
