package ru.practicum.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Getter
public enum EventState {

    PENDING,

    PUBLISHED,

    CANCELED;

    public static List<EventState> fromListString(List<String> states) {
        List<EventState> result = new ArrayList<>();
        if (states == null || states.isEmpty()) {
            return result;
        }
        return states.stream().map(EventState::valueOf).collect(Collectors.toList());
    }
}
