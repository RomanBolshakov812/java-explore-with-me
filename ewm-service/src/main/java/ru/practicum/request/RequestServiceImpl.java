package ru.practicum.request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.error.exception.IncorrectRequestParametersException;
import ru.practicum.event.EventRepository;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.model.Status;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        // Проверка, что такой запрос еще не создан
        if (requestRepository.existsRequestByRequesterIdAndEventId(userId, eventId)) {
            throw new IncorrectRequestParametersException("This request has already been created!");
        }
        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Event with id=" + eventId  + " was not found"));
        // Событие должно быть опубликовано
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new IncorrectRequestParametersException("Only published events can by changed!");
        }
        // Инициатор не может создать запрос
        if (event.getInitiator().getId().equals(userId)) {
            throw new IncorrectRequestParametersException("The event initiator cannot "
                    + "create a request!");
        }
        // Превышается количество участников
        if (event.getParticipantLimit() != 0) {
            if (event.getConfirmedRequests().equals(event.getParticipantLimit())) {
                throw new IncorrectRequestParametersException("Number of participants exceeded!");
            }
        }
        if (event.getParticipantLimit() == 0) {
            request.setStatus(Status.CONFIRMED.name());
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        } else if (event.getRequestModeration()) {
            request.setStatus(Status.PENDING.name());
        } else {
            request.setStatus(Status.CONFIRMED.name());
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }
        User requester = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User with id=" + userId  + " was not found"));
        eventRepository.save(event);
        request.setEvent(event);
        request.setRequester(requester);
        requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(request);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new EntityNotFoundException("Request with id=" + requestId + " was not found"));
        if (!request.getRequester().getId().equals(userId)) {
            throw new ValidationException("Only event initiator can cancel request!");
        }
        Event event = request.getEvent();
        event.setConfirmedRequests(event.getConfirmedRequests() - 1);
        request.setStatus("CANCELED");
        eventRepository.save(event);
        requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(request);
    }

    // Получение информации о всех запросах текущего пользователя на участие в событиях
    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsByUser(Long userId) {
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        List<ParticipationRequestDto> requestDtoList = new ArrayList<>();
        if (requests.size() > 0) {
            requestDtoList = RequestMapper.toParticipationRequestDtoList(requests);
        }
        return requestDtoList;
    }

    // Получение информации о запросах на участие в событии текущего пользователя
    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsByUserEvent(Long initiatorId, Long eventId) {
        List<Request> requests = requestRepository.findAllByInitiatorEventId(eventId, initiatorId);
        List<ParticipationRequestDto> requestDtoList = new ArrayList<>();
        if (requests.size() > 0) {
            requestDtoList = RequestMapper.toParticipationRequestDtoList(requests);
        }
        return requestDtoList;
    }

    // Изменение статуса (подтверждение, отмена) заявок на участие в событии текущего пользователя
    @Override
    public EventRequestStatusUpdateResult confirmationOfRequests(
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
            Long userId,
            Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Event with id=" + eventId + " was not found"));
        int availableLimitOfParticipants = 0; // Остаток свободных мест
        boolean participantsLimit = event.getParticipantLimit() > 0; // Есть лимит участников
        if (participantsLimit) {
            availableLimitOfParticipants =
                    event.getParticipantLimit() - event.getConfirmedRequests();
        }
        List<Request> requests = requestRepository
                .findRequestsWhereIdInIds(eventId, userId, eventRequestStatusUpdateRequest
                        .getRequestIds());
        String status = eventRequestStatusUpdateRequest.getStatus();
        EventRequestStatusUpdateResult eventRequestStatusUpdateResult
                = new EventRequestStatusUpdateResult();
        if (status.equals("CONFIRMED")) {
            // Проверка, что количество одобренных запросов не больше количества оставшихся мест
            // (при наличии лимита количества участников)
            if (participantsLimit
                    && eventRequestStatusUpdateRequest.getRequestIds().size()
                    > availableLimitOfParticipants) {
                throw new IncorrectRequestParametersException("The participant limit "
                        + "has been reached");
            }
            // Присвоение всем запросам статуса CONFIRMED
            eventRequestStatusUpdateResult
                    .setConfirmedRequests(changeRequestStatus(requests, status));
            // Увеличение в ивенте количества одобренных запросов на участие
            event.setConfirmedRequests(event.getConfirmedRequests()
                    + eventRequestStatusUpdateRequest.getRequestIds().size());
            eventRepository.save(event);
        } else {
            eventRequestStatusUpdateResult
                    .setRejectedRequests(changeRequestStatus(requests, status));
        }
        return eventRequestStatusUpdateResult;
    }

    private List<ParticipationRequestDto> changeRequestStatus(
            List<Request> requests,
            String status) {
        List<ParticipationRequestDto> participationRequestDtoList = new ArrayList<>();
        for (Request request : requests) {
            // Проверка - на попытку отклонения уже одобренного запроса
            if (status.equals("REJECTED")) {
                if (request.getStatus().equals("CONFIRMED")) {
                    throw new IncorrectRequestParametersException("Attempting to reject "
                            + "an approved application");
                }
            }
            request.setStatus(status);
            ParticipationRequestDto participationRequestDto
                    = RequestMapper.toParticipationRequestDto(request);
            participationRequestDtoList.add(participationRequestDto);
        }
        requestRepository.saveAll(requests);
        return participationRequestDtoList;
    }
}
