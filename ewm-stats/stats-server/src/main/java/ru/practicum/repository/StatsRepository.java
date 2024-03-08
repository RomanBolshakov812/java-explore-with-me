package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.model.Hit;

import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Integer> {

    List<Hit> getStats(String start, String end, List<String> uris, Boolean unique, Pageable pageable);
}
