package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Hit;

@Repository
public interface StatsRepository extends JpaRepository<Hit, Long>, StatsRepositoryCustom {

}
