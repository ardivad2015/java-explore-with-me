package ru.practicum.dto.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class EventSearchDto {

    private List<Long> usersIds;
    private List<EventState> states;
    private List<Long> categoriesIds;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private String text;
    private Boolean paid;
    private Boolean onlyAvailable;
    private SortType sort;
    private int from;
    private int size;
}
