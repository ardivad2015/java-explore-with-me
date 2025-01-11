package ru.practicum.service.event.util;

import ru.practicum.dto.event.EventDtos;
import ru.practicum.dto.event.EventMapper;
import ru.practicum.model.Event;

import java.util.Map;

public class EventProcessing {

    public static void fillAdditionalInfo(Event event, Map<Long, Long> views, Map<Long, Long> confirmedRequests) {
        event.setViews(views.getOrDefault(event.getId(), 0L));
        event.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0L));
    }

    public static EventDtos makeEventDto(Event event, Map<Long, Long> views, Map<Long, Long> confirmedRequests,
                                         boolean full, EventMapper eventMapper) {
        final EventDtos eventDtos = new EventDtos();
        fillAdditionalInfo(event, views, confirmedRequests);
        if (full) {
            eventDtos.setEventFullDto(eventMapper.toEventFullDto(event));
        } else {
            eventDtos.setEventShortDto(eventMapper.toEventShortDto(event));
        }
        return eventDtos;
    }
}
