package ru.practicum.repository.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;

import java.util.List;

@Repository
public interface EventRepositoryCustom {

    List<Event> findAllByInitiatorIdWithCategoryAndInitiator(Long userId, int offset, int limit);
}
