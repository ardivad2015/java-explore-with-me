package ru.practicum.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class BadRequestException extends RuntimeException {
        public BadRequestException(String message) {
            super(message);
        }
}

