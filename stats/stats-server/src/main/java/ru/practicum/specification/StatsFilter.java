package ru.practicum.specification;

import java.util.List;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatsFilter {
    private String start;
    private String end;
    private List<String> uris;
}
