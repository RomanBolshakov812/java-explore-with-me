package ru.practicum.event.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.EventService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserEventController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public EventFullDto addEvent(
            @RequestBody @Validated NewEventDto newEventDto,
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
            @RequestBody @Validated UpdateEventUserRequest updateEventUserRequest,
            @PathVariable("userId") @NonNull Long userId,
            @PathVariable("eventId") @NonNull Long eventId) {
        return eventService.updateEventByCurrentUser(updateEventUserRequest, userId, eventId);
    }
}
