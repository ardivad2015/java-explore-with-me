package ru.practicum.repository.compilation;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import ru.practicum.dto.compilation.CompilationSearchDto;
import ru.practicum.dto.event.EventSearchDto;
import ru.practicum.dto.event.SortType;
import ru.practicum.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


public class CompilationRepositoryCustomImpl extends QuerydslRepositorySupport implements CompilationRepositoryCustom {

    private final QCompilation compilation = QCompilation.compilation;

    public CompilationRepositoryCustomImpl() {
        super(Compilation.class);
    }

    @Override
    public List<Compilation> findAllBySearchRequestWithNestedEntitiesEagerly(CompilationSearchDto searchDto) {
        final BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (Objects.nonNull(searchDto.getPinned())) {
            booleanBuilder.and(compilation.pinned.eq(searchDto.getPinned()));
        }

        return from(compilation)
                .leftJoin(compilation.events, QEvent.event).fetchJoin()
                .leftJoin(QEvent.event.initiator,QUser.user).fetchJoin()
                .leftJoin(QEvent.event.category,QCategory.category).fetchJoin()
                .where(booleanBuilder.getValue())
                .limit(searchDto.getSize())
                .offset(searchDto.getFrom())
                .fetch();
    }

    @Override
    public Compilation findByIdWithNestedEntitiesEagerly(Long compId) {
        return from(compilation)
                .leftJoin(compilation.events, QEvent.event).fetchJoin()
                .leftJoin(QEvent.event.initiator,QUser.user).fetchJoin()
                .leftJoin(QEvent.event.category,QCategory.category).fetchJoin()
                .where(compilation.id.eq(compId))
                .fetchOne();
    }
}
