package ru.practicum.repository.event;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import ru.practicum.dto.event.EventSearchDto;
import ru.practicum.dto.event.SortType;
import ru.practicum.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


public class EventRepositoryCustomImpl extends QuerydslRepositorySupport implements EventRepositoryCustom {

    private final QEvent event = QEvent.event;
    private final OrderSpecifier<LocalDateTime> eventOrderSpecifier = event.eventDate.asc();

    public EventRepositoryCustomImpl() {
        super(Event.class);
    }

    @Override
    public List<Event> findAllByIdInWithCategoryAndUserEagerly(List<Long> eventIds) {
        return from(event)
                .innerJoin(event.category, QCategory.category).fetchJoin()
                .innerJoin(event.initiator, QUser.user).fetchJoin()
                .where(event.id.in(eventIds))
                .fetch();
    }

    @Override
    public List<Event> findAllBySearchRequest(EventSearchDto eventSearchDto) {
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

        final String text = eventSearchDto.getText();
        if (Objects.nonNull(text) && !text.isBlank()) {
           booleanBuilder.and(event.annotation.containsIgnoreCase(text).or(event.description.containsIgnoreCase(text)));
        }

        if (Objects.nonNull(eventSearchDto.getPaid())) {
           booleanBuilder.and(event.paid.eq(eventSearchDto.getPaid()));
        }

         final JPQLQuery<Event> query = from(event)
                .innerJoin(event.category, QCategory.category).fetchJoin()
                .innerJoin(event.initiator, QUser.user).fetchJoin()
                .where(booleanBuilder.getValue());

        final boolean sortByViews = Objects.nonNull(eventSearchDto.getSort()) &&
                eventSearchDto.getSort().equals(SortType.VIEWS);

        if (!eventSearchDto.getOnlyAvailable() && !sortByViews) {
            query.orderBy(eventOrderSpecifier)
                    .limit(eventSearchDto.getSize())
                    .offset(eventSearchDto.getFrom());
        }

        return query.fetch();
    }
}
