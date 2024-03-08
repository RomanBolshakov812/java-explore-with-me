package ru.practicum.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.StatsDto;
import ru.practicum.service.StatsService;
import java.time.format.DateTimeFormatter;


@RestController
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StatsController {

    private final StatsService statsService;
    private static  final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostMapping(path = "/hit")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void addHit(@RequestBody HitDto hitDto) {
        statsService.addHit(hitDto);
    }

    @GetMapping(path = "/stats")
    public ResponseEntity<List<StatsDto>> getStats(
            @RequestParam @NonNull String start,
            @RequestParam @NonNull String end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {

        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        try {
            startDateTime = LocalDateTime.parse(start, DATE_TIME_FORMATTER);
            endDateTime = LocalDateTime.parse(end, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException exception) {
            return ResponseEntity.badRequest().build();
        }

        List<StatsDto> viewStats = statsService.getStats(startDateTime, endDateTime, uris, unique);

        return ResponseEntity.ok(viewStats);
    }
}
