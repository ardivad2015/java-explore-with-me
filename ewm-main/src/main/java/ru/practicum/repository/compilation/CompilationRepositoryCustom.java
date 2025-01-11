package ru.practicum.repository.compilation;

import org.springframework.stereotype.Repository;
import ru.practicum.dto.compilation.CompilationSearchDto;
import ru.practicum.dto.event.EventSearchDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.List;

@Repository
public interface CompilationRepositoryCustom {

    List<Compilation> findAllBySearchRequestWithNestedEntitiesEagerly(CompilationSearchDto searchDto);

    Compilation findByIdWithNestedEntitiesEagerly(Long compId);
}
