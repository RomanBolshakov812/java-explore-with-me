package ru.practicum.error.exception;

public class IncorrectEntityParametersException extends RuntimeException{
    public IncorrectEntityParametersException(final String message) {
        super(message);
    }
}
