package ru.practicum.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>,
        JpaSpecificationExecutor<Event> {
    Page<Event> findEventsByInitiatorId(Long userId, Pageable page);

    Long countEventsByCategoryId(Long catId);

    Event findByIdAndByState(Long id, Enum<State> stateEnum);
}
