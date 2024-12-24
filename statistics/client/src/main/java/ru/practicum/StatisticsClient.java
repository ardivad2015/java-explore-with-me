package ru.practicum;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class StatisticsClient {

    private final RestTemplate restTemplate;
    private final String statsServerUrl;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatisticsClient(RestTemplate restTemplate, String statsServerUrl) {
        this.restTemplate = restTemplate;
        this.statsServerUrl = statsServerUrl;
    }

    public ResponseEntity<List<ViewStatsDto>> getStats(LocalDateTime start, LocalDateTime end, List<String> uris,
                                                       Boolean unique) {
        final StringBuilder urisString = new StringBuilder();
        boolean firstUri = true;
        for (String uri : uris) {
           if (!firstUri) {
               urisString.append(",");
           }
           urisString.append(uri);
            if (firstUri) {
                firstUri = false;
            }
        }

        final Map<String, Object> parameters = Map.of(
                "start", start.format(dateTimeFormatter),
                "end", end.format(dateTimeFormatter),
                "uris", urisString.toString(),
                "unique", unique
        );
        final String uri = statsServerUrl + "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
        final HttpEntity<Object> requestEntity = new HttpEntity<>(null, defaultHeaders());

        return restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
                new ParameterizedTypeReference<List<ViewStatsDto>>() {}, parameters);
    }

    public ResponseEntity<StatsHitDto> save(StatsHitDto statsHitDto) {
        final String uri = statsServerUrl + "/hit";
        final HttpEntity<StatsHitDto> requestEntity = new HttpEntity<>(statsHitDto, defaultHeaders());

        return restTemplate.exchange(uri, HttpMethod.POST,
                requestEntity, StatsHitDto.class);
    }

    private  HttpHeaders defaultHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}

