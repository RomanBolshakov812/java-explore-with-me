package ru.practicum.request;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.Request;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    boolean existsRequestByRequesterIdAndEventId(Long requester, Long event);
    List<Request> findAllByRequesterId(Long requesterId);
}
