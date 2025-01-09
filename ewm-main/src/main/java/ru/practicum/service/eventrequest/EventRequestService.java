package ru.practicum.service.eventrequest;

import org.springframework.stereotype.Repository;
import ru.practicum.dto.eventrequest.EventRequestDto;

import java.util.List;

@Repository
public interface EventRequestService {

    EventRequestDto addNew(Long userId, Long eventId);

    List<EventRequestDto> getAllByEventIdFromPrivate(Long userId, Long eventId);


}
