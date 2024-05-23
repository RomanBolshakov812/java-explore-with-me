package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.event.model.Location;

@Getter
@Setter
public class UpdateEventAdminRequest extends UpdateEventUserRequest {

    public UpdateEventAdminRequest(
            String annotation,
            Long category,
            String description,
            String eventDate,
            Location location,
            Boolean paid,
            Integer participantLimit,
            Boolean requestModeration,
            String stateAction,
            String title) {
        super(annotation, category, description, eventDate, location, paid, participantLimit,
                requestModeration, stateAction, title);
    }
}
