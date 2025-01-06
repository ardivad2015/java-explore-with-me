package ru.practicum.service.event;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;

import java.time.LocalDateTime;

public interface EventService {

    EventFullDto addNew(Long userId, NewEventDto newEventDto);

    EventFullDto getFullById(Long userId, Long eventId);

    EventShortDto getAllByInitiator(Long userId, int from, int size);
}
