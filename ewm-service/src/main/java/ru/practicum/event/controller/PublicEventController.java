package ru.practicum.event.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.EventService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.State;
import ru.practicum.event.specification.EventFilter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events", produces = MediaType.APPLICATION_JSON_VALUE)
public class PublicEventController {
    private final EventService eventService;
    private static  final DateTimeFormatter DATE_TIME_FORMATTER///////////////////////////////////////////////////
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity<List<EventFullDto>> getEventsByFilter(
            @RequestParam(value = "users", required = false) List<Long> users,
            @RequestParam(value = "states", required = false) List<State> states,
            @RequestParam(value = "categories", required = false) List<Long> categories,
            @RequestParam(value = "rangeStart", required = false) String rangeStart,
            @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(1000) Integer size) {

        EventFilter filter = new EventFilter();
        filter.setUsers(users);
        filter.setStates(states);
        filter.setCategories(categories);
        filter.setRangeStart(rangeStart);
        filter.setRangeEnd(rangeEnd);

        List<EventFullDto> events = eventService.searchEventsByAdmin(filter, from, size);
        return ResponseEntity.ok(events);
    }

//    @PatchMapping("/{eventId}")
//    @ResponseStatus(code = HttpStatus.OK)
//    public EventFullDto updateEventByAdmin(
//            @RequestBody UpdateEventAdminRequest updateEventAdminRequest,
//            @PathVariable("eventId") @NonNull Long eventId) {
//        return eventService.updateEventByAdmin(updateEventAdminRequest, eventId);
//    }
}
