package ru.practicum.repository.event;

import org.springframework.stereotype.Repository;
import ru.practicum.dto.event.EventSearchDto;
import ru.practicum.model.Event;

import java.util.List;

@Repository
public interface EventRepositoryCustom {

    Event findByIdWithNestedEntitiesEagerly(Long eventId);

    List<Event> findAllBySearchRequest(EventSearchDto eventSearchDto);
}
