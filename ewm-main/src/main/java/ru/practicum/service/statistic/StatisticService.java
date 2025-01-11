package ru.practicum.service.statistic;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.StatsHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.Event;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface StatisticService {

    void saveEndpointHit(HttpServletRequest request);

    Map<Long, Long> getStatsByEvents(Collection<Event> events);

}
