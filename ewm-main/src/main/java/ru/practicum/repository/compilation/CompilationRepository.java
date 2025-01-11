package ru.practicum.repository.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Compilation;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long>, CompilationRepositoryCustom {

    boolean existsByTitle(String title);
}
