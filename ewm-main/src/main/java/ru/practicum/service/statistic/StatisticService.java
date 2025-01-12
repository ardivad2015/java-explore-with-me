package ru.practicum.service.statistic;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.model.Event;
import java.util.Collection;
import java.util.Map;

public interface StatisticService {

    void saveEndpointHit(HttpServletRequest request);

    Map<Long, Long> getStatsByEvents(Collection<Event> events);
}
