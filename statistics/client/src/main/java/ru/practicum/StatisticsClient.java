package ru.practicum;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class StatisticsClient {

    private final RestTemplate restTemplate;
    private final String statsServerUrl;

    public StatisticsClient(RestTemplate restTemplate, String statsServerUrl) {
        this.restTemplate = restTemplate;
        this.statsServerUrl = statsServerUrl;
    }

    public ResponseEntity<List<ViewStatsDto>> getStats(String start, String end, List<String> uris, Boolean unique) {
        final Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        final String uri = statsServerUrl + "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
        final HttpEntity<Object> requestEntity = new HttpEntity<>(null, defaultHeaders());

        ResponseEntity<List<ViewStatsDto>> serverResponse = restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
                (Class<List<ViewStatsDto>>)(Class<?>)List.class, parameters);
        return prepareResponse(serverResponse);
    }

    public ResponseEntity<StatsHitDto> save(StatsHitDto statsHitDto) {
        final String uri = statsServerUrl + "/hit";
        final HttpEntity<StatsHitDto> requestEntity = new HttpEntity<>(statsHitDto, defaultHeaders());

        ResponseEntity<StatsHitDto> serverResponse = restTemplate.exchange(uri, HttpMethod.POST,
                requestEntity, StatsHitDto.class);

        return prepareResponse(serverResponse);
    }

    private  HttpHeaders defaultHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private <T> ResponseEntity<T> prepareResponse(ResponseEntity<T> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        final ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        return responseBuilder.build();
    }

}

