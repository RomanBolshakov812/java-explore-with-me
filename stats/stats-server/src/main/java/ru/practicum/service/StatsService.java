package ru.practicum.service;

import java.util.List;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.specification.StatsFilter;

public interface StatsService {

    void addHit(EndpointHit endpointHit);

    List<ViewStats> getStats(StatsFilter filter, Boolean unique);
}
