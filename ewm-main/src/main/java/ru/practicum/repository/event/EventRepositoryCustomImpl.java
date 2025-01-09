package ru.practicum.repository.event;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.event.EventAdminSearchDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.model.QCategory;
import ru.practicum.model.QEvent;
import ru.practicum.model.QUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


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

    @Override
    public List<Event> getAllBySearchRequest(EventAdminSearchDto eventSearchDto) {
        final BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (Objects.nonNull(eventSearchDto.getUsersIds())) {
            booleanBuilder.and(event.initiator.id.in(eventSearchDto.getUsersIds()));
        }

        if (Objects.nonNull(eventSearchDto.getStates())) {
            booleanBuilder.and(event.state.in(eventSearchDto.getStates()));
        }

        if (Objects.nonNull(eventSearchDto.getCategoriesIds())) {
            booleanBuilder.and(event.category.id.in(eventSearchDto.getCategoriesIds()));
        }

        final LocalDateTime rangeStart = Objects.nonNull(eventSearchDto.getRangeStart()) ?
                eventSearchDto.getRangeStart().withNano(0) : LocalDateTime.now().withNano(0);
        booleanBuilder.and(event.eventDate.goe(rangeStart));

        if (Objects.nonNull(eventSearchDto.getRangeEnd())) {
            booleanBuilder.and(event.eventDate.loe(eventSearchDto.getRangeEnd()));
        }

        return from(event)
                .innerJoin(event.category, QCategory.category).fetchJoin()
                .innerJoin(event.initiator, QUser.user).fetchJoin()
                .where(booleanBuilder.getValue())
                .orderBy(eventOrderSpecifier)
                .limit(eventSearchDto.getSize())
                .offset(eventSearchDto.getFrom())
                .fetch();
    }

//        final String text = eventSearchDto.getText();
//        if (Objects.nonNull(text) && !text.isBlank()) {
//            booleanBuilder.and(event.annotation.containsIgnoreCase(text));
//        }
//
//        if (Objects.nonNull(eventSearchDto.getPaid())) {
//            booleanBuilder.and(event.paid.eq(eventSearchDto.getPaid()));
//        }
//
//        if (Objects.nonNull(eventSearchDto.getOnlyPublished()) && eventSearchDto.getOnlyPublished()) {
//            booleanBuilder.and(event.publishedOn.isNotNull());
//        }

}
