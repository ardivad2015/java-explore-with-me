package ru.practicum.dto.event;

import org.mapstruct.Mapper;
import ru.practicum.dto.category.CategoryMapper;
import ru.practicum.dto.user.UserMapper;
import ru.practicum.dto.venue.VenueMapper;
import ru.practicum.model.Event;

@Mapper(componentModel = "spring", uses = {VenueMapper.class, CategoryMapper.class, UserMapper.class})
public interface EventMapper {

    Event toEvent(NewEventDto newEventDto);

    EventFullDto toEventFullDto(Event event);

    EventShortDto toEventShortDto(Event event);
}
