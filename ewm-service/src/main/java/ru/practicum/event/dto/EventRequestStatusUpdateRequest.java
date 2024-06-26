package ru.practicum.event.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private String status;
}
