package ru.practicum.event.controller;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.EventService;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.State;
import ru.practicum.event.specification.EventFilter;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    // Поиск событий
    public ResponseEntity<List<EventFullDto>> searchEventsByAdmin(
            @RequestParam(value = "users", required = false) List<Long> users,
            @RequestParam(value = "states", required = false) List<State> states,
            @RequestParam(value = "categories", required = false) List<Long> categories,
            @RequestParam(value = "rangeStart", required = false) String rangeStart,
            @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        EventFilter filter = new EventFilter();
        filter.setUsers(users);
        filter.setStates(states);
        filter.setCategories(categories);
        filter.setOnlyAvailable(false);
        filter.setRangeStart(rangeStart);
        filter.setRangeEnd(rangeEnd);

        List<EventFullDto> events = eventService.searchEventsByAdmin(filter, from, size);
        return ResponseEntity.ok(events);
    }

    // Редактирование данных события и его статуса (отклонение/публикация)
    @PatchMapping("/{eventId}")
    @ResponseStatus(code = HttpStatus.OK)
    public EventFullDto updateEventByAdmin(
            @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest,
            @PathVariable("eventId") @NonNull @Positive Long eventId) {
        return eventService.updateEventByAdmin(updateEventAdminRequest, eventId);
    }
}
