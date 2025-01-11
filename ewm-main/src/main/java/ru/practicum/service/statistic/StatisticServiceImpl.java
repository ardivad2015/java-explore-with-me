package ru.practicum.service.statistic;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.StatisticsClient;
import ru.practicum.StatsHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final StatisticsClient statisticsClient;
    private final String GET_EVENT_ENDPOINT = "/events/";
    @Value("${ewm.app.name}")
    private String APP_NAME;

    @Override
    public void saveEndpointHit(HttpServletRequest request) {
        final StatsHitDto statsHitDto = new StatsHitDto();

        statsHitDto.setIp(request.getRemoteAddr());
        statsHitDto.setUri(request.getRequestURI());
        statsHitDto.setApp(APP_NAME);
        statsHitDto.setTimestamp(LocalDateTime.now());

        try {
            statisticsClient.save(statsHitDto);
        } catch (Exception ignored) {
        }
    }

    @Override
    public Map<Long, Long> getStatsByEvents(Collection<Event> events) {
        final List<Event> publishedEvents = events.stream()
                .filter(event -> Objects.nonNull(event.getPublishedOn()))
                .sorted(Comparator.comparing(Event::getPublishedOn))
                .toList();
        final Map<Long, Long> eventViews = new HashMap<>();

        if (publishedEvents.isEmpty()) {
            return eventViews;
        }

        final List<String> uris = publishedEvents.stream().map(event -> GET_EVENT_ENDPOINT + event.getId()).toList();
        final LocalDateTime start = publishedEvents.get(0).getPublishedOn();

        List<ViewStatsDto> viewStatsDtoList;
        try {
            viewStatsDtoList = statisticsClient.getStats(start, LocalDateTime.now(), uris, true).getBody();
        } catch (Exception e) {
            return eventViews;
        }

        if (viewStatsDtoList == null) {
            return eventViews;
        }

        viewStatsDtoList.forEach(viewDto -> {
            try {
                Long eventId = Long.valueOf(viewDto.getUri().replace(GET_EVENT_ENDPOINT, ""));
                eventViews.put(eventId, eventViews.getOrDefault(eventId, 0L) + viewDto.getHits());
            } catch (NumberFormatException ignored) {
            }
        });

        return eventViews;
    }
}
