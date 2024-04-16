package ru.practicum.specification;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.model.Hit;

@Component
public class HitSpecification {

    public Specification<Hit> build(StatsFilter filter) {
        return getByTimestamp(filter.getStart(), filter.getEnd())
                .and(getByUri(filter.getUris()));
    }

    private Specification<Hit> getByTimestamp(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> cb.between(root.get("timestamp"), start, end);
    }

    private Specification<Hit> getByUri(List<String> uris) {

        if (uris != null) {
            return (root, query, cb) -> {
                Path<String> uri = root.get("uri");
                return uri.in(uris);
            };
        } else {
            return null;
        }
    }
}
