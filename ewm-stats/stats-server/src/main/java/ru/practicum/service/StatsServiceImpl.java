package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.StatsDto;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.Hit;
import ru.practicum.repository.StatsRepository;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public void addHit(HitDto hitDto) {
        Hit hit = HitMapper.toHit(hitDto);
        statsRepository.save(hit);
    }

    @Override
    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        //Pageable page = hitsToPage(from, size);
        if (uris == null) {
            uris = new ArrayList<>();
        }

        return statsRepository.getStats(start, end, uris, unique).toList();
    }

//    private  Pageable hitsToPage(Integer from, Integer size) {
//        Sort sort = Sort.by(Sort.Direction.ASC, "hits");
//        int startPage = from / size;
//        return PageRequest.of(startPage, size, sort);
//    }
}
