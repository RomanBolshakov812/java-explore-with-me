package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
//@NoArgsConstructor
@AllArgsConstructor
public class StatsDto {
//    example: ewm-main-service
//    Идентификатор сервиса для которого записывается информация
    private String app;

//    example: /events/1
//    URI сервиса
    private String uri;

    private Integer hits;
//    example: 6
//    Количество просмотров
}
