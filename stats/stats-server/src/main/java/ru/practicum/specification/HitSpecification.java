package ru.practicum.specification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.model.Hit;

@Component
public class HitSpecification {
    private static  final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Specification<Hit> build(StatsFilter filter) {
        List<Specification<Hit>> specifications = new ArrayList<>();
        specifications.add(filter.getUris() == null ? null : uriIn(filter.getUris()));
        specifications.add(byTimestamp(filter.getStart(), filter.getEnd()));
        specifications = specifications.stream().filter(Objects::nonNull)
                .collect(Collectors.toList());
        return specifications.stream().reduce(Specification::and).orElse(null);
    }

    private Specification<Hit> byTimestamp(String start, String end) {

        try {
            LocalDateTime startDateTime;
            LocalDateTime endDateTime;
            startDateTime = LocalDateTime.parse(start, DATE_TIME_FORMATTER);
            endDateTime = LocalDateTime.parse(end, DATE_TIME_FORMATTER);
            if (startDateTime.equals(endDateTime)) {
                return (root, query, cb) -> cb.equal(root.get("ts"), startDateTime);
            } else {
                return (root, query, cb) ->
                        cb.between(root.get("ts"), startDateTime, endDateTime);
            }
        } catch (DateTimeParseException exception) {
            throw new RuntimeException("Incorrect date value!");
        }
    }

    private Specification<Hit> uriIn(List<String> uris) {
        return (root, query, cb) -> cb.in(root.get("uri")).value(uris);
    }
}
