package ru.practicum.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public enum StateAction {

    PUBLISH_EVENT,
    REJECT_EVENT,
    SEND_TO_REVIEW,
    CANCEL_REVIEW;

    public static List<StateAction> fromListString(List<String> states) {
        List<StateAction> result = new ArrayList<>();
        if (states == null || states.isEmpty()) {
            return result;
        }
        return states.stream().map(StateAction::valueOf).collect(Collectors.toList());
    }
}
