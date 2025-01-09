package ru.practicum.repository.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCustom {

    @Query(
            value = "select location_distance(:lat1, :lon1, :lat2, :lon2)",
            nativeQuery = true
    )
    float distance(@Param("lat1") float lat1, @Param("lon1") float lon1,
                          @Param("lat2") float lat2, @Param("lon2") float lon2);

    Boolean existsByCategoryId(Long category_Id);
}
