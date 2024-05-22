package ru.practicum.compilation.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto_event.EventShortDto;
import ru.practicum.event.model.Event;

public class CompilationMapper {
    public static CompilationDto toCompilationDto(Compilation compilation,
                                    List<EventShortDto> eventShortDtoList) {
        return new CompilationDto(
                compilation.getId(),
                eventShortDtoList,
                compilation.getPinned(),
                compilation.getTitle()
        );
    }

    public static Compilation toCompilation(NewCompilationDto newCompilationDto,
                                            List<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setEvents(events);
        compilation.setPinned(newCompilationDto.getPinned());
        compilation.setTitle(newCompilationDto.getTitle());
        return compilation;
    }

    public static List<CompilationDto> toCompilationDtoList(
            List<Compilation> compilations,
            HashMap<Long, List<EventShortDto>> eventShortDtoList) {
        List<CompilationDto> list = new ArrayList<>();
        for (Compilation compilation : compilations) {
            List<EventShortDto> shortDtoList = new ArrayList<>();
            if (eventShortDtoList.size() != 0) {
                shortDtoList = eventShortDtoList.get(compilation.getId());
            }
            CompilationDto compilationDto = toCompilationDto(compilation, shortDtoList);
            list.add(compilationDto);
        }
        return list;
    }
}
