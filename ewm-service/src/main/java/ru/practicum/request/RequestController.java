package ru.practicum.request;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/request")
public class RequestController {

    RequestService requestService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(
            @PathVariable("userId") @NonNull @Positive Long userId,
            @RequestParam (value = "eventId") @NonNull @Positive Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

}
