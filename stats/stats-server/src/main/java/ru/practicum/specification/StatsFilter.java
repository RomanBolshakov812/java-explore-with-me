package ru.practicum.specification;

import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

//@Data/////////////////////////////////////////////////////////////////////////////////
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatsFilter {
    private String start;
    private String end;
    private List<String> uris;
}
