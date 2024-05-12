package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.user.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;/////////////////////////////////////////////////
    private final RequestRepository requestRepository;
    private static  final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        if (requestRepository.existsRequestByRequesterIdAndEventId(userId, eventId)) {
            throw new ValidationException("This request has already been created!");
        }
        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Event with id=" + eventId  + " was not found"));
        // Событие должно быть опубликовано
        // Инициатор не может создать реквест
        // К-во одобренных заявок равно лимиту запросов
        if (!event.getState().equals(State.PUBLISHED)
                | event.getInitiator().getId().equals(userId)
                | event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throw new ValidationException("Only published events can by changed!");
        }
        // Если нет премодерации - request CONFIRMED
        if (event.getRequestModeration()) {
            request.setStatus("PENDING");
        } else {
            request.setStatus("CONFIRMED");
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }
        eventRepository.save(event);
        request.setEventId(eventId);
        request.setRequesterId(userId);
        requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(request);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new EntityNotFoundException("Request with id=" + requestId + " was not found"));
        if (!request.getRequesterId().equals(userId)) {
            throw new ValidationException("Only event initiator can cancel request!");
        }
        Long eventId = request.getEventId();
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Event with id=" + eventId + " was not found"));
        event.setConfirmedRequests(event.getConfirmedRequests() - 1);
        request.setStatus("CANCELED");
        eventRepository.save(event);
        requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByUser(Long userId) {
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        List<ParticipationRequestDto> requestDtoList = new ArrayList<>();
        if (requests.size() > 0) {
            requestDtoList = RequestMapper.toParticipationRequestDtoList(requests);
        }
        return requestDtoList;
    }
}
