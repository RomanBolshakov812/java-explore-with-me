package ru.practicum.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.ViewStats;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.Hit;
import ru.practicum.repository.HitRepository;
import ru.practicum.specification.HitSpecification;
import ru.practicum.specification.StatsFilter;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    @Autowired
    private final HitRepository hitRepository;
    @Autowired
    private final HitSpecification hitSpecification;

    @Override
    public void addHit(HitDto hitDto) {
        Hit hit = HitMapper.toHit(hitDto);
        hitRepository.save(hit);
    }

    @Override
    public List<ViewStats> getStats(StatsFilter filter, Boolean unique) {
        Specification<Hit> specification = hitSpecification.build(filter);
        List<Hit> hitList = hitRepository.findAll(specification);
        return HitMapper.toViewStatsList(hitList, unique);
    }
}
