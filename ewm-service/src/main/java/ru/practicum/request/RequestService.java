package ru.practicum.request;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

@Transactional
public interface RequestService {
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    @Transactional(readOnly = true)
    List<ParticipationRequestDto> getRequestsByUser(Long userId);

    @Transactional(readOnly = true)
    List<ParticipationRequestDto> getRequestsByUserEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult confirmationOfRequests(
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
            Long userId,
            Long eventId);
}
