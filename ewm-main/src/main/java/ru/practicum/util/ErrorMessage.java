package ru.practicum.util;

public final class ErrorMessage {

    public static String userNotFoundMessage(Long userId) {
        return String.format("Пользователь с id = %d не найден", userId);
    }

    public static String categoryNotFoundMessage(Long categoryId) {
        return String.format("Категория с id = %d не найдена", categoryId);
    }

    public static String eventNotFoundMessage(Long eventId) {
        return String.format("Событие с id = %d не найдено", eventId);
    }

    public static String eventRequestNotFoundMessage(Long requestId) {
        return String.format("Запрос на участие с id = %d не найден", requestId);
    }

    public static String compilationNotFoundMessage(Long compId) {
        return String.format("Подборка с id = %d не найдена", compId);
    }

    public static String compilationWithTitleExists(String title) {
        return String.format("Подборка с заголовком  %s уже существует", title);
    }
}
