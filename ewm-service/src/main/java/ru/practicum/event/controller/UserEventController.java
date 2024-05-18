package ru.practicum.event.controller;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.EventService;
import ru.practicum.event.dto.*;
import ru.practicum.request.RequestService;
import ru.practicum.request.dto.ParticipationRequestDto;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserEventController {

    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public EventFullDto addEvent(
            @RequestBody @Valid NewEventDto newEventDto,
            @PathVariable("userId") @NonNull @Positive Long userId) {
        return eventService.addEvent(newEventDto, userId);
    }

    // Получение событий, добавленных текущим пользователем
    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<EventShortDto> getEventsByUser(
            @PathVariable("userId") @NonNull Long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(1000) Integer size) {
        return eventService.getEventsByUser(userId, from, size);
    }

    // Получение полной информации о событии, добавленном текущим пользователем
    @GetMapping("/{eventId}")
    @ResponseStatus(code = HttpStatus.OK)
    public EventFullDto getEventByUser(
            @PathVariable("userId") @NonNull Long userId,
            @PathVariable("eventId") @NonNull Long eventId) {
        return eventService.getEventByCurrentUser(userId, eventId);
    }

    // Изменение события, добавленного текущим пользователем
    @PatchMapping("/{eventId}")
    @ResponseStatus(code = HttpStatus.OK)
    public EventFullDto updateEventByCurrentUser(
            @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest,
            @PathVariable("userId") @NonNull Long userId,
            @PathVariable("eventId") @NonNull Long eventId) {
        return eventService.updateEventByCurrentUser(updateEventUserRequest, userId, eventId);
    }

    // Получение информации о запросах на участие в событии текущего пользователя
    @GetMapping("/{eventId}/requests")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsByUserAndByEvent(
            @PathVariable("userId") @NonNull Long userId,
            @PathVariable("eventId") @NonNull Long eventId) {
        return requestService.getRequestsByUserEvent(userId, eventId);
    }

    // Изменение статуса (подтверждение, отмена) заявок на участие в событии текущего пользователя
    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(code = HttpStatus.OK)
    public EventRequestStatusUpdateResult confirmationOfRequests(
            @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
            @PathVariable("userId") @NonNull Long userId,
            @PathVariable("eventId") @NonNull Long eventId) {
        return requestService.confirmationOfRequests(
                eventRequestStatusUpdateRequest,
                userId,
                eventId);
    }
}
