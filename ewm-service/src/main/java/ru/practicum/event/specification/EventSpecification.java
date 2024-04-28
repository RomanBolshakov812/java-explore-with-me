package ru.practicum.event.specification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.error.exception.IncorrectRequestParametersException;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;

@Component
public class EventSpecification {

    private static  final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Specification<Event> build(EventFilter filter) {
        List<Specification<Event>> specifications = new ArrayList<>();
        specifications.add(filter.getUsers() == null ? null : userIn(filter.getUsers()));
        specifications.add(filter.getStates() == null ? null : stateIn(filter.getStates()));
        specifications.add(filter.getCategories() == null ? null : categoryIn(filter.getCategories()));
        specifications.add(timestampBetween(filter.getRangeStart(), filter.getRangeEnd()));

        specifications = specifications.stream().filter(Objects::nonNull).collect(Collectors.toList());
        return specifications.stream().reduce(Specification::and).orElse(null);
    }

    private Specification<Event> userIn(List<Long> users) {

        return (root, query, cb) -> cb.in((root.get("initiator")).get("id")).value(users);
    }

    private Specification<Event> stateIn(List<State> states) {
        return (root, query, cb) -> cb.in(root.get("state")).value(states);
    }

    private Specification<Event> categoryIn(List<Long> categories) {
        return (root, query, cb) -> cb.in(root.get("category").get("id")).value(categories);
    }

    private Specification<Event> timestampBetween(String rangeStart, String rangeEnd) {
        LocalDateTime start;
        LocalDateTime end;

        try {
            if (rangeStart != null) {
                start = LocalDateTime.parse(rangeStart, DATE_TIME_FORMATTER);
            } else start = null;
            if (rangeEnd != null) {
                end = LocalDateTime.parse(rangeEnd, DATE_TIME_FORMATTER);
            } else end = null;
        } catch (DateTimeParseException exception) {
            throw new IncorrectRequestParametersException("Incorrect date value!");
        }

        if (start != null & end != null) {
            return (root, query, cb) -> cb.between(root.get("eventDate"), start, end);
        } else if (start == null & end != null) {
            return (root, query, cb) -> cb.lessThan(root.get("eventDate"), end);
        } else if (start != null) {
            return (root, query, cb) -> cb.greaterThan(root.get("eventDate"), start);
        }
        else return null;
    }
}
