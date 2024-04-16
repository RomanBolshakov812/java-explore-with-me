package ru.practicum.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private final HitRepository hitRepository;
    @Autowired
    private final HitSpecification hitSpecification;

    @Override
    public void addHit(EndpointHit endpointHit) {
        Hit hit = HitMapper.toHit(endpointHit);
        hitRepository.save(hit);
    }

    @Override
    public List<ViewStats> getStats(StatsFilter filter, Boolean unique,
                                    Integer from, Integer size) {
        Pageable page = statsToPage(from, size);
        Specification<Hit> specification = hitSpecification.build(filter);
        List<Hit> hitList = hitRepository.findAll(specification, page).toList();
        return HitMapper.toViewStatsList(hitList, unique);
    }

    private  Pageable statsToPage(Integer from, Integer size) {
        int startPage = from / size;
        return PageRequest.of(startPage, size);
    }
}
