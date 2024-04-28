package ru.practicum.event.model;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public enum State {
    PENDING,
    PUBLISHED,
    CANCELED,
    SEND_TO_REVIEW,
    CANCEL_REVIEW,
    PUBLISH_EVENT,
    REJECT_EVENT
}
