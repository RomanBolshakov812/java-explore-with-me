package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.error.exception.DependentEntitiesException;
import ru.practicum.event.EventRepository;
import ru.practicum.event.EventServiceImpl;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.util.PageMaker;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventServiceImpl eventService;

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        List<Event> events = new ArrayList<>();
        if (newCompilationDto.getEvents() != null && newCompilationDto.getEvents().size() > 0) {
            events = eventRepository.findAllWhereIdInIds(newCompilationDto.getEvents());
            HashMap<Long, Long> views = eventService.getViews(events, null, null);
            eventShortDtoList = EventMapper.toEventShortDtoList(events, views);
        }
        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto,events);
        compilationRepository.save(compilation);
        return CompilationMapper
                .toCompilationDto(compilation, eventShortDtoList);
    }

    @Override
    public void deleteCompilation(Long compId) {
        compilationRepository.findById(compId).orElseThrow(() ->
                new EntityNotFoundException("Compilation with id=" + compId + " was not found"));
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest,
                                            Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new EntityNotFoundException("Compilation with id=" + compId + " was not found"));
        if (updateCompilationRequest.getEvents() != null
                && updateCompilationRequest.getEvents().size() > 0) {
            List<Event> events =
                    eventRepository.findAllWhereIdInIds(updateCompilationRequest.getEvents());
            compilation.setEvents(events);
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        HashMap<Long, Long> views =
                eventService.getViews(compilation.getEvents(), null, null);
        List<EventShortDto> eventShortDtoList =
                getEventShortDtoList(compilation.getEvents(), views);
        compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilation, eventShortDtoList);
    }

    // Получение подборок событий
    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable page = PageMaker.toPage(from, size);
        List<CompilationDto> compilationDtoList = new ArrayList<>();
        List<Compilation> compilations = new ArrayList<>();///////////////////////////////////////////////////
        if (pinned == null) {
            compilations = compilationRepository.findAll(page).toList();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, page).toList();
        }
        if (compilations.size() == 0) {
            return compilationDtoList;
        } else {
            Set<Event> allEvents = new HashSet<>();
            for (Compilation compilation : compilations) {
                allEvents.addAll(compilation.getEvents());
            }
            ArrayList<Event> events = new ArrayList<>(allEvents);
            HashMap<Long, Long> views = eventService.getViews(events, null, null);

            // Список шортов для каждой подборки
            HashMap<Long, List<EventShortDto>> eventShortDtoList = new HashMap<>();
            for (Compilation compilation : compilations) {
                getEventShortDtoList(compilation.getEvents(), views);
            }
            return CompilationMapper.toCompilationDtoList(compilations, eventShortDtoList);
        }
    }

    // Получение подборки событий по её id
    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new EntityNotFoundException("Compilation with id=" + compId + " was not found"));
        List<Event> events = compilation.getEvents();
        HashMap<Long, Long> views = eventService.getViews(events, null, null);
        List<EventShortDto> eventShortDtoList = getEventShortDtoList(events, views);
        return CompilationMapper.toCompilationDto(compilation, eventShortDtoList);
    }

    private  List<EventShortDto> getEventShortDtoList(List<Event> events,
                                                      HashMap<Long, Long> views) {
        List<EventShortDto> eventShortDtoList;
        eventShortDtoList = EventMapper.toEventShortDtoList(events, views);
        return eventShortDtoList;
    }
}
