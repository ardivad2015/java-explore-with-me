package ru.practicum.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.NumberExpression;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.Hit;
import ru.practicum.model.QHit;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class StatsRepositoryCustomImpl extends QuerydslRepositorySupport implements StatsRepositoryCustom {

    private final QHit hit = QHit.hit;

    public StatsRepositoryCustomImpl() {
        super(Hit.class);
    }

    @Override
    public List<ViewStatsDto> findAllByUriInAndTimestampBetween(LocalDateTime start, LocalDateTime end,
                                                                List<String> uris, boolean unique) {
        final BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(hit.timestamp.between(start, end));
        if (Objects.nonNull(uris) && !uris.isEmpty()) {
            booleanBuilder.and(hit.uri.in(uris));
        }

        final NumberExpression<Long> countExpression = unique ? hit.ip.countDistinct() : hit.ip.count();

        return from(hit)
                .select(hit.app, hit.uri, countExpression)
                .where(booleanBuilder.getValue())
                .groupBy(hit.app, hit.uri)
                .orderBy(countExpression.desc())
                .fetch().stream()
                .map(tuple -> new ViewStatsDto(tuple.get(0, String.class), tuple.get(1, String.class),
                        tuple.get(2, Long.class))).toList();
    }
}
