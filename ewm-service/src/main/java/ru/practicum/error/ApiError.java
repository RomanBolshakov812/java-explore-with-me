package ru.practicum.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiError {
    private final String status;
    private final String reason;
    private final String message;
    private final String timestamp;
}
