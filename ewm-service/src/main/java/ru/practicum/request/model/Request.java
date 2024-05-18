package ru.practicum.request.model;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "requests")
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
    @Column(name = "event", nullable = false)
    private Long eventId;
    @Column(name = "requester", nullable = false)
    private Long requesterId;
    @Column(name = "status", nullable = false)
    private String status;
}
