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
        specifications.add(timestampBetween(filter.getStart(), filter.getEnd()));

        specifications = specifications.stream().filter(Objects::nonNull).collect(Collectors.toList());
        return specifications.stream().reduce(Specification::and).orElse(null);
    }

    private Specification<Hit> timestampBetween(String start, String end) {

        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        try {
            startDateTime = LocalDateTime.parse(start, DATE_TIME_FORMATTER);
            endDateTime = LocalDateTime.parse(end, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException exception) {
            throw new RuntimeException("Incorrect date value!");/////////////////////////////////////////////////////////////////////////
        }
        return (root, query, cb) -> cb.between(root.get("timestamp"), startDateTime, endDateTime);
    }

    private Specification<Hit> uriIn(List<String> uris) {
        return (root, query, cb) -> cb.in(root.get("uri")).value(uris);
    }
//    private static  final DateTimeFormatter DATE_TIME_FORMATTER
//            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//    public Specification<Hit> build(StatsFilter filter) {
//        return getByTimestamp(filter.getStart(), filter.getEnd())
//                .and(getByUri(filter.getUris()));
//    }
//
//    private Specification<Hit> getByTimestamp(String start, String end) {
//
//        LocalDateTime startDateTime;
//        LocalDateTime endDateTime;
//        try {
//            startDateTime = LocalDateTime.parse(start, DATE_TIME_FORMATTER);
//            endDateTime = LocalDateTime.parse(end, DATE_TIME_FORMATTER);
//        } catch (DateTimeParseException exception) {
//            throw new RuntimeException("Incorrect date value!");/////////////////////////////////////////////////////////////////////////
//        }
//        return (root, query, cb) -> cb.between(root.get("timestamp"), startDateTime, endDateTime);
//    }
//
//    private Specification<Hit> getByUri(List<String> uris) {
//
//        if (uris != null) {
//            return (root, query, cb) -> {
//                Path<String> uri = root.get("uri");
//                return uri.in(uris);
//            };
//        } else {
//            return null;
//        }
//    }
}
