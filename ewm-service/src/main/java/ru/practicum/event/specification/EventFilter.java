package ru.practicum.event.specification;

import lombok.*;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.event.model.State;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.List;

//@Data/////////////////////////////////////////////////////////////////////////
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
