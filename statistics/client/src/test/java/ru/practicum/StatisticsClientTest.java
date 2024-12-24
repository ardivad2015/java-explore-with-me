package ru.practicum;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StatisticsClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Captor
    ArgumentCaptor<Map<String, Object>> mapArgumentCaptor;
    @Captor
    ArgumentCaptor<HttpEntity<StatsHitDto>> httpEntityArgumentCaptor;

    @Test
    void getStats_thenCalledRestTemplate() {
        final StatisticsClient statisticsClient = new StatisticsClient(restTemplate, "");
        final LocalDateTime start = LocalDateTime.of(2024,12, 24, 0, 0,0);
        final LocalDateTime end = start.plusDays(1);
        final List<String> uris = new ArrayList<>();
        final boolean unique = true;

        uris.add("/test/1");
        uris.add("/test/2");

        statisticsClient.getStats(start, end, uris, unique);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class), mapArgumentCaptor.capture());

        final Map<String, Object> parameters = mapArgumentCaptor.getValue();

        assertEquals(parameters.get("start"), start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        assertEquals(parameters.get("end"), end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        assertEquals(parameters.get("uris"), "/test/1,/test/2");
        assertEquals(parameters.get("unique"), unique);
    }

    @Test
    void save_thenCalledRestTemplate() {
        final StatisticsClient statisticsClient = new StatisticsClient(restTemplate, "");
        final StatsHitDto statsHitDto = new StatsHitDto();
        statsHitDto.setIp("192.168.1.1");
        statsHitDto.setApp("test");
        statsHitDto.setUri("/test/1");
        statsHitDto.setTimestamp(LocalDateTime.now());

        statisticsClient.save(statsHitDto);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), httpEntityArgumentCaptor.capture(),
                eq(StatsHitDto.class));
        final HttpEntity<StatsHitDto> httpEntity = httpEntityArgumentCaptor.getValue();
        assertEquals(httpEntity.getBody(), statsHitDto);
    }
}