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

    @Query(nativeQuery = true, value = "select * from requests r where r.event = "
            + "(select e.id from events e where e.initiator_id = ?1 and e.id = ?2)")
    List<Request> findAllByInitiatorEventId(Long initiatorId, Long eventId);

    @Query(nativeQuery = true, value = "select * from requests r where r.event "
            + "= (select e.id from events e where e.id = ?2 and e.initiator_id = ?1) "
            + "and r.id in ?3")
    List<Request> findRequestsWhereIdInIds(Long userId, Long eventId, List<Long> ids);
}
