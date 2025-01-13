package ru.practicum.dto.eventrequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.model.EventRequest;

@Mapper(componentModel = "spring")
public interface EventRequestMapper {

    @Mapping(target = "requester", source = "user.id")
    @Mapping(target = "event", source = "event.id")
    EventRequestDto toEventRequestDto(EventRequest request);
}
