package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.State;
import ru.practicum.user.dto.NewUserRequest;

import javax.annotation.Nullable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import javax.validation.executable.ValidateOnExecution;

@Getter
@Setter
@AllArgsConstructor
//@NoArgsConstructor
public class UpdateEventUserRequest {
    // Почему-то не работает ни Size, ни Length
    //@Size(min = 3, max = 120)
    @Length(min = 20, max = 2000)
    private String annotation;
    private Long category;
    //@Size(min = 3, max = 120)
    @Length(min = 20, max = 7000)
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid;
    @PositiveOrZero
    private Integer participantLimit;
    private Boolean requestModeration;
    private String stateAction;
    @Length(min = 3, max = 120)
    //@Size(min = 3, max = 120)
    private String title;
}


//public class UpdateEventUserRequest extends NewEventDto {
//
//
//    private String stateAction;
//
//    public UpdateEventUserRequest(
//            @Length(min = 20, max = 2000)
//            String annotation,
//            Long category,
//            @Length(min = 20, max = 7000)
//            String description,
//            String eventDate,
//            Location location,
//            Boolean paid,
//            @PositiveOrZero
//            Integer participantLimit,
//            Boolean requestModeration,
//            String stateAction,
//            @Length(min = 3, max = 120)
//            String title) {
//        super(annotation, category, description, eventDate, location, paid, participantLimit,
//                requestModeration, title);
//        this.stateAction = stateAction;
//    }
//}
