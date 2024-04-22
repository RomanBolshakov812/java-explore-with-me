package ru.practicum.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.service.StatsService;
import ru.practicum.specification.StatsFilter;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StatsController {

    private final StatsService statsService;
    private static  final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostMapping(path = "/hit")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void addHit(@RequestBody EndpointHit endpointHit) {
        statsService.addHit(endpointHit);
    }

    @GetMapping(path = "/stats")
    public ResponseEntity<List<ViewStats>> getStats(
            @RequestParam("start") @NonNull String start,
            @RequestParam("end") @NonNull String end,
            @RequestParam(value = "uris", required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {

        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        try {
            startDateTime = LocalDateTime.parse(start, DATE_TIME_FORMATTER);
            endDateTime = LocalDateTime.parse(end, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException exception) {
            return ResponseEntity.badRequest().build();
        }

        StatsFilter filter = new StatsFilter();
        filter.setStart(startDateTime);
        filter.setEnd(endDateTime);
        filter.setUris(uris);

        List<ViewStats> viewStats = statsService.getStats(filter, unique);

        return ResponseEntity.ok(viewStats);
    }
}
