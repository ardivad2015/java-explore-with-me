package ru.practicum.dto.event;

import org.mapstruct.Mapper;
import ru.practicum.model.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {

    Event toEvent(NewEventDto newEventDto);

    EventFullDto toEventFullDto(Event event);

    EventShortDto toEventShortDto(Event event);
}
