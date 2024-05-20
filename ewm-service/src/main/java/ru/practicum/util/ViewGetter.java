package ru.practicum.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import ru.practicum.ViewStats;
import ru.practicum.client.StatsClient;
import ru.practicum.event.model.Event;

public class ViewGetter {

    private static  final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static HashMap<Long, Long> getViews(StatsClient statsClient, List<Event> events,
                                               String start, String end) {
        if (start == null) {
            start = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        }
        if (end == null) {
            end = "9999-12-31 23:59:59";
        }
        List<Long> idsList = new ArrayList<>(); // Список айдишников
        for (Event event : events) {
            idsList.add(event.getId());
        }
        List<String> uris = new ArrayList<>(); // Список урлов
        for (Long eventId : idsList) {
            uris.add("/events/" + eventId);
        }
        ArrayList<ViewStats> viewStats = statsClient.getStats(start, end, uris, false);
        HashMap<Long, Long> views = new HashMap<>();
        if (viewStats.size() != 0) {
            for (Long eventId : idsList) {
                views.put(eventId, viewStats.get(0).getHits());
                viewStats.remove(0);
            }
        }
        return views;
    }
}
