package ru.practicum.error;

import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.ConflictRelationsConstraintException;
import ru.practicum.exception.ConflictUniqueConstraintException;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<String> errorList = new ArrayList<>();
        errorList.addAll(e.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("Ошибка валидации поля %s: %s. некорректное значение %s",
                        error.getField(), error.getDefaultMessage(),
                        Objects.isNull(error.getRejectedValue()) ? "" : error.getRejectedValue().toString()))
                .toList());
        errorList.addAll(e.getBindingResult().getGlobalErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList());
        return new ErrorResponse(HttpStatus.BAD_REQUEST, "Incorrectly made request.", errorList.toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onConstraintViolationException(ConstraintViolationException e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST, "Incorrectly made request.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse onConflictUniqueConstraintException(ConflictUniqueConstraintException e) {
        return new ErrorResponse(HttpStatus.CONFLICT, "Integrity constraint has been violated.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse onConflictRelationsConstraintExceptionException(ConflictRelationsConstraintException e) {
        return new ErrorResponse(HttpStatus.CONFLICT, "For the requested operation the conditions are not met.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse onNotFoundException(NotFoundException e) {
        return new ErrorResponse(HttpStatus.NOT_FOUND, "The required object was not found.",
                e.getMessage());
    }
}