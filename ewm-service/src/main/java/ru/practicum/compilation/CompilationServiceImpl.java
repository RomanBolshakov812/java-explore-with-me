package ru.practicum.compilation;

import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.CommentRepository;
import ru.practicum.event.EventRepository;
import ru.practicum.event.dto_event.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.util.CommentAdder;
import ru.practicum.util.PageMaker;
import ru.practicum.util.ViewGetter;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {

    private final StatsClient statsClient;
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        List<Event> events = new ArrayList<>();
        if (newCompilationDto.getEvents() != null && newCompilationDto.getEvents().size() > 0) {
            events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
            HashMap<Long, Long> views
                    = ViewGetter.getViews(statsClient, events, null, null);
            List<Comment> allComments = getComments(events);
            eventShortDtoList = EventMapper.toEventShortDtoList(
                    events,
                    views,
                    CommentAdder.addCommentsOfEventDto(events, allComments));
        }
        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, events);
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
                    eventRepository.findAllByIdIn(updateCompilationRequest.getEvents());
            compilation.setEvents(events);
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        HashMap<Long, Long> views =
                ViewGetter.getViews(statsClient, compilation.getEvents(), null, null);
        List<Comment> comments = getComments(compilation.getEvents());
        List<EventShortDto> eventShortDtoList =
                getEventShortDtoList(compilation.getEvents(), views, comments);
        compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilation, eventShortDtoList);
    }

    // Получение подборок событий
    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable page = PageMaker.toPage(from, size);
        List<CompilationDto> compilationDtoList = new ArrayList<>();
        List<Compilation> compilations;
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
            HashMap<Long, Long> views
                    = ViewGetter.getViews(statsClient, events, null, null);
            // Список шортов для каждой подборки
            HashMap<Long, List<EventShortDto>> eventShortDtoList = new HashMap<>();
            for (Compilation compilation : compilations) {
                List<Comment> allComments = getComments(compilation.getEvents());
                getEventShortDtoList(compilation.getEvents(), views, allComments);
            }
            return CompilationMapper.toCompilationDtoList(compilations, eventShortDtoList);
        }
    }

    // Получение подборки событий по её id
    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new EntityNotFoundException("Compilation with id=" + compId + " was not found"));
        List<Event> events = compilation.getEvents();
        HashMap<Long, Long> views = ViewGetter.getViews(statsClient, events, null, null);
        List<Comment> comments = getComments(events);
        List<EventShortDto> eventShortDtoList = getEventShortDtoList(events, views, comments);
        return CompilationMapper.toCompilationDto(compilation, eventShortDtoList);
    }

    private  List<EventShortDto> getEventShortDtoList(List<Event> events,
                                                      HashMap<Long, Long> views,
                                                      List<Comment> comments) {
        List<EventShortDto> eventShortDtoList;
        eventShortDtoList = EventMapper.toEventShortDtoList(
                events,
                views,
                CommentAdder.addCommentsOfEventDto(events, comments));
        return eventShortDtoList;
    }

    private List<Comment> getComments(List<Event> events) {
        List<Long> eventIds = new ArrayList<>();
        for (Event event : events) {
            eventIds.add(event.getId());
        }
        return commentRepository.findAllByEventIdIn(eventIds);
    }
}
