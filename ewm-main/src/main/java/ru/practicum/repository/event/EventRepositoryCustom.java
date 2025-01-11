package ru.practicum.repository.event;

import org.springframework.stereotype.Repository;
import ru.practicum.dto.event.EventSearchDto;
import ru.practicum.model.Event;

import java.util.List;

@Repository
public interface EventRepositoryCustom {

    List<Event> findAllByIdInWithCategoryAndUserEagerly(List<Long> eventIds);

    List<Event> findAllBySearchRequest(EventSearchDto eventSearchDto);
}
