package ru.practicum.event.dto_event;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.request.dto.ParticipationRequestDto;

@Getter
@Setter
public class EventRequestStatusUpdateResult {
    private List<ParticipationRequestDto> confirmedRequests;
    private List<ParticipationRequestDto> rejectedRequests;
}
