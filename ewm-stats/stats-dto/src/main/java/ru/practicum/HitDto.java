package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
//@NoArgsConstructor
@AllArgsConstructor
public class HitDto {
//    example: ewm-main-service
//    Идентификатор сервиса для которого записывается информация
    private String app;

//    example: /events/1
//    URI для которого был осуществлен запрос
    private String uri;

//    example: 192.163.0.1
//    IP-адрес пользователя, осуществившего запрос
    private String ip;

//    example: 2022-09-06 11:00:23
//    Дата и время, когда был совершен запрос к эндпоинту (в формате "yyyy-MM-dd HH:mm:ss")
    //private String timeStamp;
    private String timestamp;
}
