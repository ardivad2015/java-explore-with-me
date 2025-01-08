package ru.practicum.service.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventFullDto addNew(Long userId, NewEventDto newEventDto);

    EventFullDto getByIdToUser(Long userId, Long eventId);

    List<EventShortDto> getAllByInitiator(Long userId, int from, int size);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventFullDto> getAllToAdmin(List<Long> usersIds, List<EventState> states, List<Long> categoriesIds,
                                  LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);


    float calcDistance(float lat1, float lon1, float lat2, float lon2);
}
