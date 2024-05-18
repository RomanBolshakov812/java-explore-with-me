package ru.practicum.compilation.controller;

import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.CompilationService;
import ru.practicum.compilation.dto.CompilationDto;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class PublicCompilationController {

    private final CompilationService compilationService;

    // Получение подборок событий
    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<CompilationDto> getCompilations(
            @RequestParam(value = "pinned", required = false) Boolean pinned,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10")
            @Min(1) @Max(10000000) Integer size) {
        return compilationService.getCompilations(pinned, from, size);
    }

    // Получение подборки событий по её id
    @GetMapping("/{compId}")
    @ResponseStatus(code = HttpStatus.OK)
    public CompilationDto getCompilationById(
            @PathVariable("compId") @NonNull Long compId) {
        return compilationService.getCompilationById(compId);
    }
}
