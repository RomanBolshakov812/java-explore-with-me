package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "hits")
@AllArgsConstructor
@NoArgsConstructor
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;// А НУЖНО ЛИ ВООБЩЕ ЦИФРОВОЕ id ?????????????????????????????????????????????????????????????????????????

    @Column(name = "app", nullable = false)
    private String app;

    @Column(name = "uri", nullable = false)
    private String uri;

    @Column(name = "ip", nullable = false)
    private String ip;

    @Column(name = "time_point", nullable = false)
    private LocalDateTime timeStamp;//  ВОЗМОЖНО ПРИДЕТСЯ ПЕРЕДЕЛАТЬ В СТРОКУ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
}
