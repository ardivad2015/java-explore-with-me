package ru.practicum.exception;

public class ConflictUniqueConstraintException extends RuntimeException {
    public ConflictUniqueConstraintException(String message) {
        super(message);
    }
}
