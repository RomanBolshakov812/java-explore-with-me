package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.request.dto.ParticipationRequestDto;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {


        return null;
    }
}
