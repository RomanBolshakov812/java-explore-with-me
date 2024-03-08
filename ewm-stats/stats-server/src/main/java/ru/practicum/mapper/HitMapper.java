package ru.practicum.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import ru.practicum.HitDto;
import ru.practicum.StatsDto;
import ru.practicum.model.Hit;

public class HitMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Hit toHit(HitDto hitDto) {
        Hit hit = new Hit();
        hit.setApp(hitDto.getApp());
        hit.setUri(hitDto.getUri());
        hit.setIp(hitDto.getIp());
        hit.setTimeStamp(LocalDateTime.parse(hitDto.getTimestamp(), DATE_TIME_FORMATTER));
        return hit;
    }

    public static HitDto toHitDto(Hit hit) {// МОЖЕТ НЕ ПОНАДОБИТСЯ ??????????????????????????????????????????????????????
        return new HitDto(
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                hit.getTimeStamp().toString()
        );
    }

    public static StatsDto viewStatsDto(Hit hit, Integer hits) {
        return new StatsDto(
                hit.getApp(),
                hit.getUri(),
                hits
        );
    }
}
