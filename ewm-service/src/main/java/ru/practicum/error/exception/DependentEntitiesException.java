package ru.practicum.error.exception;

public class DependentEntitiesException extends RuntimeException {
    public DependentEntitiesException(final String message) {
        super(message);
    }
}
