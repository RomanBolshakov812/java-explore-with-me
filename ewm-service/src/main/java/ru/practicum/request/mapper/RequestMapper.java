package ru.practicum.request.mapper;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;

public class RequestMapper {
    private static  final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return new ParticipationRequestDto(
                request.getId(),
                request.getCreated().format(DATE_TIME_FORMATTER),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus()
        );
    }

    public static List<ParticipationRequestDto> toParticipationRequestDtoList(
            List<Request> requests) {
        List<ParticipationRequestDto> list = new ArrayList<>();
        for (Request request : requests) {
            ParticipationRequestDto participationRequestDto = toParticipationRequestDto(request);
            list.add(participationRequestDto);
        }
        return list;
    }
}
