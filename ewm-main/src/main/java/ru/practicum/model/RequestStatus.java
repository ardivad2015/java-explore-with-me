package ru.practicum.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum RequestStatus {

    PENDING,
    CONFIRMED,
    REJECTED,
    CANCELED;

    public static List<RequestStatus> fromListString(List<String> states) {
        List<RequestStatus> result = new ArrayList<>();
        if (states == null || states.isEmpty()) {
            return result;
        }
        return states.stream().map(RequestStatus::valueOf).collect(Collectors.toList());
    }
}
