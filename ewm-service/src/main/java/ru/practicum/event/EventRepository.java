package ru.practicum.event;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>,
        JpaSpecificationExecutor<Event> {
    Page<Event> findEventsByInitiatorId(Long userId, Pageable page);

    Long countEventsByCategoryId(Long catId);

    @Query(nativeQuery = true, value = "select * from events e where e.id in ?1")
    List<Event> findAllWhereIdInIds(List<Long> ids);
}
