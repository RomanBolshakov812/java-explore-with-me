package ru.practicum.error;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.error.exception.DependentEntitiesException;
import ru.practicum.error.exception.IncorrectRequestParametersException;

@RestControllerAdvice
public class ErrorHandler {
    // 400
    @ExceptionHandler({
            ValidationException.class,
            PersistenceException.class,
            NumberFormatException.class,
            MethodArgumentNotValidException.class,
            ArithmeticException.class,
            DateTimeParseException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(final RuntimeException e) {
        return new ApiError(
                "BAD_REQUEST",
                "Incorrectly made request.",
                e.getMessage(),
                LocalDateTime.now().toString());
    }

    // 404
    @ExceptionHandler({EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final RuntimeException e) {
        return new ApiError(
                "NOT_FOUND",
                "The required object was not found.",
                e.getMessage(),
                LocalDateTime.now().toString());
    }

    // 409
    @ExceptionHandler({
            ConstraintViolationException.class,
            DataIntegrityViolationException.class,
            IncorrectRequestParametersException.class,
            DependentEntitiesException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final RuntimeException e) {
        return new ApiError(
                "CONFLICT",
                "Integrity constraint has been violated.",
                e.getMessage(),
                LocalDateTime.now().toString());
    }
}
