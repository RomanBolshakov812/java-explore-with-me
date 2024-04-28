package ru.practicum.event.specification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.event.model.State;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFilter {
    private List<Long> users;
    private List<State> states;
    private List<Long> categories;
    private String rangeStart;
    private String rangeEnd;
}
