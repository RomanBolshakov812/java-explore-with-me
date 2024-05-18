package ru.practicum.compilation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "compilations")
@AllArgsConstructor
@NoArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "events_compilations",
//          joinColumns = {@JoinColumn(name = "event_id")},//////////////////////////////////////////////////////////
//          inverseJoinColumns = {@JoinColumn(name = "compilation_id")})
        joinColumns = {@JoinColumn(name = "compilation_id")},
        inverseJoinColumns = {@JoinColumn(name = "event_id")})
    private List<Event> events;// = new ArrayList<>();

    @Column(name = "pinned", nullable = false)
    private Boolean pinned;

    @Column(name = "title", nullable = false)
    private String title;


}
