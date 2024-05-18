package ru.practicum.request;

import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequestsByUser(Long userId);

    List<ParticipationRequestDto> getRequestsByUserEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult confirmationOfRequests(
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
            Long userId,
            Long eventId);
}
