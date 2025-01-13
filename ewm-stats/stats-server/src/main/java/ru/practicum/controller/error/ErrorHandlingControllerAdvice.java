package ru.practicum.controller.error;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.BadRequestException;

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
        return new ErrorResponse(errorList);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onBadRequestException(BadRequestException e) {
        final List<String> errorList = new ArrayList<>();
        errorList.add(e.getMessage());
        return new ErrorResponse(errorList);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onException(Exception e) {
        List<String> errorList = new ArrayList<>();
        errorList.add(e.getMessage());
        return new ErrorResponse(errorList);
    }
}