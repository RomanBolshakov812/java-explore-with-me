package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
//@NoArgsConstructor
@AllArgsConstructor
public class HitDto {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
