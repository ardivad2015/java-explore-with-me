package ru.practicum.service.event;

import ru.practicum.dto.event.*;

import java.util.List;

public interface EventService {

    EventFullDto addNew(Long userId, NewEventDto newEventDto);

    EventFullDto getByIdToUser(Long userId, Long eventId);

    List<EventShortDto> getAllByInitiator(Long userId, int from, int size);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventFullDto> getAllToAdmin(EventSearchDto eventAdminSearchDto);

    float calcDistance(float lat1, float lon1, float lat2, float lon2);
}
