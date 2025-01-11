package ru.practicum.service.eventrequest;

import org.springframework.stereotype.Repository;
import ru.practicum.dto.eventrequest.EventRequestDto;

import java.util.List;
import java.util.Map;

@Repository
public interface EventRequestService {

    EventRequestDto addNew(Long userId, Long eventId);

    List<EventRequestDto> getAllByUserId(Long userId);

    EventRequestDto cancelById(Long userId, Long requestId);

    Map<Long, Long> countConfirmedRequestByEventIds(List<Long> eventIds);
}
