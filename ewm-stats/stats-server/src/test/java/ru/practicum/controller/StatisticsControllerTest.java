package ru.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.StatsHitDto;
import ru.practicum.service.StatsService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatisticsController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StatsService statsService;

    @Captor
    ArgumentCaptor<StatsHitDto> statsHitDtoArgumentCaptor;
    @Captor
    ArgumentCaptor<LocalDateTime> localDateTimeArgumentCaptor;
    @Captor
    ArgumentCaptor<List<String>> listStringArgumentCaptor;
    @Captor
    ArgumentCaptor<Boolean> booleanArgumentCaptor;

    @SneakyThrows
    @Test
    void hitStats() {
        final StatsHitDto statsHitDto = new StatsHitDto();
        statsHitDto.setIp("192.168.1.1");
        statsHitDto.setApp("test");
        statsHitDto.setUri("/test/1");
        statsHitDto.setTimestamp(LocalDateTime.now());

        mockMvc.perform(post("/hit")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(statsHitDto)))
                .andExpect(status().isOk());

        verify(statsService).save(statsHitDtoArgumentCaptor.capture());
        final StatsHitDto savedStatsHitDto = statsHitDtoArgumentCaptor.getValue();

        assertEquals(savedStatsHitDto.getApp(), statsHitDto.getApp());
        assertEquals(savedStatsHitDto.getIp(), statsHitDto.getIp());
        assertEquals(savedStatsHitDto.getUri(), statsHitDto.getUri());
    }

    @SneakyThrows
    @Test
    void getStats_thenCalledGetStats() {
        final LocalDateTime start = LocalDateTime.of(2024,12, 24, 0, 0,0);
        final LocalDateTime end = start.plusDays(1);
        final List<String> uris = new ArrayList<>();
        final boolean unique = true;
        final StringBuilder urisString = new StringBuilder();

        uris.add("/test/1");
        uris.add("/test/2");

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

        mockMvc.perform(get("/stats")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .param("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .param("uris", urisString.toString())
                        .param("unique", String.valueOf(unique))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(statsService).getStats(localDateTimeArgumentCaptor.capture(), localDateTimeArgumentCaptor.capture(),
                listStringArgumentCaptor.capture(), booleanArgumentCaptor.capture());

        final List<LocalDateTime> savedDates = localDateTimeArgumentCaptor.getAllValues();
        final List<String> savedUris = listStringArgumentCaptor.getValue();
        final Boolean savedUnique = booleanArgumentCaptor.getValue();

        assertEquals(savedDates.get(0), start);
        assertEquals(savedDates.get(1), end);
        assertEquals(savedUris, uris);
        assertEquals(savedUnique, unique);
    }
}