package ru.practicum.event.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private String status;
}
