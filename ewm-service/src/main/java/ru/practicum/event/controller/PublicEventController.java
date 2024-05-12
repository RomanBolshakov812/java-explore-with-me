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
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.State;
import ru.practicum.event.specification.EventFilter;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events", produces = MediaType.APPLICATION_JSON_VALUE)
public class PublicEventController {
    private final EventService eventService;
    private static  final DateTimeFormatter DATE_TIME_FORMATTER///////////////////////////////////////////////////
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
            @RequestParam(value = "sort", required = false) String sort,//EVENT_DATE, VIEWS////////////////////////////
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(1000) Integer size,
            HttpServletRequest request) {

        if (rangeStart == null || rangeEnd == null) {
            rangeStart = LocalDateTime.now().format(DATE_TIME_FORMATTER);
            rangeEnd = LocalDateTime.MAX.format(DATE_TIME_FORMATTER);
        }

        EventFilter filter = new EventFilter();
        filter.setStates(new ArrayList<>(List.of(State.PUBLISHED)));
        filter.setText(text);
        filter.setCategories(categories);
        filter.setPaid(paid);
        filter.setRangeStart(rangeStart);
        filter.setRangeEnd(rangeEnd);
        filter.setOnlyAvailable(onlyAvailable);

        // Еще как-то надо обрабатывать VIEW !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // И отправлять инфу о факте запроса в СТАТИСТИКУ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

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

//    @PatchMapping("/{eventId}")
//    @ResponseStatus(code = HttpStatus.OK)
//    public EventFullDto updateEventByAdmin(
//            @RequestBody UpdateEventAdminRequest updateEventAdminRequest,
//            @PathVariable("eventId") @NonNull Long eventId) {
//        return eventService.updateEventByAdmin(updateEventAdminRequest, eventId);
//    }
}
