package ru.practicum.util;

public final class ErrorMessage {

    public static String UserNotFoundMessage(Long userId) {
        return String.format("Пользователь с id = %d не найден", userId);
    }

    public static String CategoryNotFoundMessage(Long categoryId) {
        return String.format("Категория с id = %d не найдена", categoryId);
    }

    public static String EventNotFoundMessage(Long eventId) {
        return String.format("Событие с id = %d не найдено", eventId);
    }

    public static String EventRequestNotFoundMessage(Long requestId) {
        return String.format("Запрос на участие с id = %d не найден", requestId);
    }
}
