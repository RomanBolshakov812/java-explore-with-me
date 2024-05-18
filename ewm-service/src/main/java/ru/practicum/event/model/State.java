package ru.practicum.event.model;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public enum State {
    PENDING,
    PUBLISHED,
    CANCELED
}
