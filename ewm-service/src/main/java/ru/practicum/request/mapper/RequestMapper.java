package ru.practicum.request.mapper;

import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RequestMapper {
    private static  final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

//    public  static Request toRequest(ParticipationRequestDto participationRequestDto) {
//        Request request = new Request();
//        LocalDateTime created
//                = LocalDateTime.parse(participationRequestDto.getCreated(), DATE_TIME_FORMATTER);
//        request.setCreated(created);
//        request.setEventId(participationRequestDto.getEventId());
//        request.setRequestorId(participationRequestDto.getRequestorId());
//        request.setStatus(participationRequestDto.getStatus());
//        return request;
//    }

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return new ParticipationRequestDto(
                request.getId(),
                request.getCreated().format(DATE_TIME_FORMATTER),
                request.getEventId(),
                request.getRequesterId(),
                request.getStatus()
        );
    }

    public static List<ParticipationRequestDto> toParticipationRequestDtoList(List<Request> requests) {
        List<ParticipationRequestDto> list = new ArrayList<>();
        for (Request request : requests) {
            ParticipationRequestDto participationRequestDto = toParticipationRequestDto(request);
            list.add(participationRequestDto);
        }
        return list;
    }
}
