package ru.practicum.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.model.Hit;

public class HitMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Hit toHit(EndpointHit endpointHit) {
        Hit hit = new Hit();
        hit.setApp(endpointHit.getApp());
        hit.setUri(endpointHit.getUri());
        hit.setIp(endpointHit.getIp());
        hit.setTs(LocalDateTime.parse(endpointHit.getTimestamp(), DATE_TIME_FORMATTER));
        return hit;
    }

    public static EndpointHit toHitDto(Hit hit) {
        return new EndpointHit(
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                hit.getTs().toString()
        );
    }

    public static List<EndpointHit> toHitDtoList(List<Hit> hits) {///////////////////////////////////////////////////////////////////
        List<EndpointHit> result = new ArrayList<>();
        for (Hit hit : hits) {
            result.add(toHitDto(hit));
        }
        return result;
    }

    public static List<ViewStats> toViewStatsList(List<Hit> hitList, Boolean unique) {

        List<ViewStats> viewStatsList = new ArrayList<>();
        Map<String, Long> urisAndHitsMap = new HashMap<>();

        if (unique) {
            Map<String, Set<String>> hitsWithUniqueIp = hitList.stream()
                    .collect(Collectors.groupingBy(Hit::getUri,
                            Collectors.mapping(Hit::getIp, Collectors.toSet())));
            for (String uri : hitsWithUniqueIp.keySet()) {
                urisAndHitsMap.put(uri, (long) (hitsWithUniqueIp.get(uri).size()));
            }
        } else {
            urisAndHitsMap = hitList.stream()
                    .collect(Collectors.groupingBy(Hit::getUri, Collectors.counting()));
        }

        for (String uri : urisAndHitsMap.keySet()) {
            ViewStats viewStats = new ViewStats();
            viewStats.setApp("ewm-main-service");
            viewStats.setUri(uri);
            viewStats.setHits(urisAndHitsMap.get(uri));
            viewStatsList.add(viewStats);
        }

        viewStatsList.sort(Comparator.comparingLong(ViewStats::getHits).reversed());
        return viewStatsList;
    }
}
