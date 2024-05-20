package ru.practicum.service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.specification.StatsFilter;

@Transactional
public interface StatsService {

    void addHit(EndpointHit endpointHit);

    @Transactional(readOnly = true)
    List<ViewStats> getStats(StatsFilter filter, Boolean unique);
}
