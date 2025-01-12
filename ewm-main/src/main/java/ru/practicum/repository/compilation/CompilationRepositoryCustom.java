package ru.practicum.repository.compilation;

import org.springframework.stereotype.Repository;
import ru.practicum.dto.compilation.CompilationSearchDto;
import ru.practicum.model.Compilation;
import java.util.List;

@Repository
public interface CompilationRepositoryCustom {

    List<Compilation> findAllBySearchRequestWithNestedEntitiesEagerly(CompilationSearchDto searchDto);

    Compilation findByIdWithNestedEntitiesEagerly(Long compId);
}
