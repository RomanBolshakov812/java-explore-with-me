package ru.practicum.specification;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsFilter {
    private LocalDateTime start;
    private LocalDateTime end;
    private List<String> uris;
}
