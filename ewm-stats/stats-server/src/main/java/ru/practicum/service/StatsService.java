package ru.practicum.service;

import java.util.List;
import ru.practicum.HitDto;
import ru.practicum.ViewStats;
import ru.practicum.specification.StatsFilter;

public interface StatsService {

    void addHit(HitDto hitDto);

    List<ViewStats> getStats(StatsFilter filter, Boolean unique);
}
