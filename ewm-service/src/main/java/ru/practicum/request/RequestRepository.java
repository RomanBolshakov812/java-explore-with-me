package ru.practicum.request;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.Request;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    boolean existsRequestByRequesterIdAndEventId(Long requester, Long event);

    List<Request> findAllByRequesterId(Long requesterId);

    // Получение информации о запросах на участие в событии текущего пользователя
    @Query("select r from Request r where  r.event.id = ?1 and r.event.initiator.id = ?2")
    List<Request> findAllByInitiatorEventId(Long eventId, Long initiatorId);

    // Получение заявок (id которых входят в список ids) на участие в событии текущего пользователя
    @Query("select r from Request r where r.event.id = ?1 and r.event.initiator.id = ?2 "
            + "and r.id in ?3")
    List<Request> findRequestsWhereIdInIds(Long eventId, Long initiatorId, List<Long> ids);
}

