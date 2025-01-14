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

    private List<Long> ids;
    private List<Long> usersIds;
    private List<EventState> states;
    private List<Long> categoriesIds;
    private boolean usePeriod;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private String text;
    private Boolean paid;
    private boolean onlyAvailable;
    private SortType sort;
    private int from;
    private int size;
    private boolean sortInQuery;
    private boolean pageInQuery;
    private boolean pageable;
    private Long venueId;
    private Float radius;
    private Float lat;
    private Float lon;

}
