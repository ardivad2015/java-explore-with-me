package ru.practicum.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.StatsHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.mapper.StatsMapperImpl;
import ru.practicum.model.Hit;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceImplTest {

    @Mock
    private StatsRepository statsRepository;
    @Captor
    ArgumentCaptor<Hit> hitArgumentCaptor;

    private final StatsMapper statsMapper = new StatsMapperImpl();

    @Test
    public void hitStat_callRepositorySave() {
        final StatsService statsService = new StatsServiceImpl(statsMapper, statsRepository);
        final StatsHitDto statsHitDto = new StatsHitDto();
        statsHitDto.setIp("192.168.1.1");
        statsHitDto.setApp("test");
        statsHitDto.setUri("/test/1");
        statsHitDto.setTimestamp(LocalDateTime.now());

        statsService.save(statsHitDto);

        verify(statsRepository).save(hitArgumentCaptor.capture());
        final Hit savedHit = hitArgumentCaptor.getValue();

        verify(statsRepository, Mockito.times(1))
                .save(any(Hit.class));
        assertEquals(savedHit.getIp(), statsHitDto.getIp());
        assertEquals(savedHit.getApp(), statsHitDto.getApp());
        assertEquals(savedHit.getUri(), statsHitDto.getUri());
        assertEquals(savedHit.getTimestamp(), statsHitDto.getTimestamp());
    }

    @Test
    public void getStats_ReturnedDtoList() {
        final StatsService statsService = new StatsServiceImpl(statsMapper, statsRepository);
        final StatsDto statsDto = new StatsDto("test", "/test/1", 100L);
        final LocalDateTime start = LocalDateTime.now();
        final LocalDateTime end = LocalDateTime.now();
        final List<String> uris = List.of("/test/1");
        final boolean unique = true;

        when(statsRepository.findAllByUriInAndTimestampBetween(start, end, uris, unique))
                .thenReturn(List.of(statsDto));

        List<ViewStatsDto> actualStats = statsService.getStats(start, end, uris, unique);

        assertEquals(actualStats.size(), 1);

        ViewStatsDto firstStats = actualStats.get(0);

        assertEquals(firstStats.getApp(), statsDto.getApp());
        assertEquals(firstStats.getUri(), statsDto.getUri());
        assertEquals(firstStats.getHits(), statsDto.getHits());
    }
}