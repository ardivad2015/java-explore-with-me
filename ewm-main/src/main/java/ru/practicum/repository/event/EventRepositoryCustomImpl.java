package ru.practicum.repository.event;

import com.querydsl.core.types.OrderSpecifier;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import ru.practicum.model.Event;
import ru.practicum.model.QCategory;
import ru.practicum.model.QEvent;
import ru.practicum.model.QUser;

import java.time.LocalDateTime;
import java.util.List;


public class EventRepositoryCustomImpl extends QuerydslRepositorySupport implements EventRepositoryCustom {

    private final QEvent event = QEvent.event;
    private final OrderSpecifier<LocalDateTime> eventOrderSpecifier = event.eventDate.asc();

    public EventRepositoryCustomImpl() {
        super(Event.class);
    }

    @Override
    public List<Event> findAllByInitiatorIdWithCategoryAndInitiator(Long userId, int offset, int limit) {

        return from(event)
                .innerJoin(event.category, QCategory.category).fetchJoin()
                .innerJoin(event.initiator, QUser.user).fetchJoin()
                .where(event.initiator.id.eq(userId))
                .orderBy(eventOrderSpecifier)
                .limit(limit)
                .offset(offset)
                .fetch();
    }
}
