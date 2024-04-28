package ru.practicum.error.exception;

public class IncorrectRequestParametersException extends RuntimeException {
    public IncorrectRequestParametersException(final String message) {
        super(message);
    }
}
