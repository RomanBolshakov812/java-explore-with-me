package ru.practicum.event.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.EventService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.State;
import ru.practicum.event.specification.EventFilter;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events", produces = MediaType.APPLICATION_JSON_VALUE)
public class PublicEventController {
    private final EventService eventService;
    private static  final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Получение событий с возможностью фильтрации
    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity<List<EventShortDto>> getEventsByFilter(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "categories", required = false) List<Long> categories,
            @RequestParam(value = "paid", required = false) Boolean paid,
            @RequestParam(value = "rangeStart", required = false) String rangeStart,
            @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(value = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(value = "sort", defaultValue = "EVENT_DATE") String sort,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(1000) Integer size,
            HttpServletRequest request) {
        if (rangeStart == null || rangeEnd == null) {
            rangeStart = LocalDateTime.now().format(DATE_TIME_FORMATTER);
            rangeEnd = "9999-12-31 23:59:59";
        }

        EventFilter filter = new EventFilter();
        filter.setStates(new ArrayList<>(List.of(State.PUBLISHED)));
        filter.setText(text);
        filter.setCategories(categories);
        filter.setPaid(paid);
        filter.setRangeStart(rangeStart);
        filter.setRangeEnd(rangeEnd);
        filter.setOnlyAvailable(onlyAvailable);

        List<EventShortDto> events = eventService.publicSearchEventsByFilter(filter, request,
                sort, from, size);

        return ResponseEntity.ok(events);
    }

    //Получение подробной информации об опубликованном событии по его идентификатору
    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public EventFullDto getEventById(
            @PathVariable("id") @NonNull Long id,
            HttpServletRequest request) {
        return eventService.getEventById(id, request);
    }
}
