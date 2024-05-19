package ru.practicum.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.Hit;
import ru.practicum.repository.HitRepository;
import ru.practicum.specification.HitSpecification;
import ru.practicum.specification.StatsFilter;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final HitRepository hitRepository;
    private final HitSpecification hitSpecification;
    private static  final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void addHit(EndpointHit endpointHit) {
        Hit hit = HitMapper.toHit(endpointHit);
        hitRepository.save(hit);
    }

    @Override
    public List<ViewStats> getStats(StatsFilter filter, Boolean unique) {
        LocalDateTime start = LocalDateTime.parse(filter.getStart(), DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse(filter.getEnd(), DATE_TIME_FORMATTER);
        if (start.isAfter(end)) {
            throw new ValidationException("Incorrect timestamps!");
        }
        Specification<Hit> specification = hitSpecification.build(filter);
        List<Hit> hitList = hitRepository.findAll(specification);
        return HitMapper.toViewStatsList(hitList, unique);
    }
}
