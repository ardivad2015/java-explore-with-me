package ru.practicum.repository.eventrequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.eventrequest.RequestsCountDto;
import ru.practicum.model.EventRequest;
import ru.practicum.model.RequestStatus;

import java.util.List;

@Repository
public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {

    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    Integer countByEventIdAndStatus(Long eventId, RequestStatus status);

    @Query("select new ru.practicum.dto.eventrequest.RequestsCountDto(er.event.id, count(er.id))" +
            "from EventRequest as er "+
            "where er.event.id in :ids and er.status = :status "+
            "group by er.event.id")
    List<RequestsCountDto> countRequestsByEventIdsAndStatus(@Param("ids") List<Long> ids,
                                                             @Param("status") RequestStatus status);

    List<EventRequest> findAllByEventIdOrderByCreatedAsc(Long eventId);

    List<EventRequest> findAllByEventIdAndStatus(Long eventId, RequestStatus status);

    List<EventRequest> findAllByUserIdOrderByCreatedAsc(Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update EventRequest er set er.status = :status where er.id in :ids")
    void updateStatusesByIds(@Param("ids") List<Long> ids, @Param("status") RequestStatus status);
}
