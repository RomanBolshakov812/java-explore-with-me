package ru.practicum.event.specification;

import java.util.List;
import lombok.*;
import ru.practicum.event.model.State;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventFilter {
    private String text;
    private List<Long> users;
    private List<State> states;
    private List<Long> categories;
    private Boolean paid;
    private String rangeStart;
    private String rangeEnd;
    private Boolean onlyAvailable;
}
