package ru.practicum.request;

import java.util.List;
import javax.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class RequestController {

    private final RequestService requestService;

    // Добавление запроса от текущего пользователя на участие в событии
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(
            @PathVariable("userId") @NonNull @Positive Long userId,
            @RequestParam (value = "eventId") @NonNull @Positive Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    // Отмена своего запроса на участие в событии
    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(code = HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(
            @PathVariable Long userId,
            @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

    // Получение информации о заявках текущего пользователя на участие в чужих событиях
    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsByUser(
            @PathVariable("userId") @NonNull Long userId) {
        return requestService.getRequestsByUser(userId);
    }
}
