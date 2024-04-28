package ru.practicum.request;

import ru.practicum.request.dto.ParticipationRequestDto;

public interface RequestService {
    ParticipationRequestDto createRequest(Long userId, Long eventId);
}
