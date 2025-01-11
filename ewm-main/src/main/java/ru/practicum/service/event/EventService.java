package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.dto.event.*;
import ru.practicum.dto.eventrequest.EventRequestStatusUpdateRequest;
import ru.practicum.dto.eventrequest.EventRequestStatusUpdateResult;
import ru.practicum.dto.eventrequest.EventRequestDto;

import java.util.List;

public interface EventService {

    EventFullDto addNew(Long userId, NewEventDto newEventDto);

    EventFullDto getByIdFromPrivate(Long userId, Long eventId);

    EventFullDto getByIdFromPublic(Long eventId, HttpServletRequest httpServletRequest);

    List<EventShortDto> getAllByInitiator(Long userId, int from, int size);

    EventFullDto updateEventFromPrivate(Long userId, Long eventId, UpdateEventUserRequest updateRequest);

    EventFullDto updateEventFromAdmin(Long eventId, UpdateEventAdminRequest updateRequest);

    List<EventFullDto> getAllFromAdmin(EventSearchDto eventSearchDto);

    List<EventShortDto> getAllFromPublic(EventSearchDto eventSearchDto, HttpServletRequest httpServletRequest);

    List<EventRequestDto> getAllRequestsByEventIdFromPrivate(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsStatuses(Long userId, Long eventId,
                                                                EventRequestStatusUpdateRequest
                                                                   eventRequestStatusUpdateRequest);

    void test();

    float calcDistance(float lat1, float lon1, float lat2, float lon2);
}
