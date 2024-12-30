package ru.practicum.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.dto.StatsDto;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class StatsRepositoryCustomImplTest {

    @Autowired
    StatsRepository statsRepository;

    @Test
    void findAllByUriInAndTimestampBetween_whenUnique_thenReturnedUnique() {

        final Hit hit1 = new Hit();
        hit1.setApp("app");
        hit1.setUri("/test/1");
        hit1.setIp("127.0.0.1");
        hit1.setTimestamp(LocalDateTime.now());

        final Hit hit2 = new Hit();
        hit2.setApp("app");
        hit2.setUri("/test/1");
        hit2.setIp("127.0.0.1");
        hit2.setTimestamp(LocalDateTime.now());

        final Hit hit3 = new Hit();
        hit3.setApp("app");
        hit3.setUri("/test/2");
        hit3.setIp("192.168.1.1");
        hit3.setTimestamp(LocalDateTime.now());

        statsRepository.save(hit1);
        statsRepository.save(hit2);
        statsRepository.save(hit3);

        List<StatsDto> stats = statsRepository.findAllByUriInAndTimestampBetween(LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(2), List.of("/test/1", "/test/2"), true);

        assertEquals(stats.size(), 2);
        for (StatsDto stat : stats) {
            assertEquals(stat.getHits(), 1);
        }
    }

    @Test
    void findAllByUriInAndTimestampBetween_whenNotUnique_thenReturnedNotUniqueWithSort() {

        final Hit hit1 = new Hit();
        hit1.setApp("app");
        hit1.setUri("/test/1");
        hit1.setIp("127.0.0.1");
        hit1.setTimestamp(LocalDateTime.now());

        final Hit hit2 = new Hit();
        hit2.setApp("app");
        hit2.setUri("/test/1");
        hit2.setIp("127.0.0.1");
        hit2.setTimestamp(LocalDateTime.now());

        final Hit hit3 = new Hit();
        hit3.setApp("app");
        hit3.setUri("/test/2");
        hit3.setIp("192.168.1.1");
        hit3.setTimestamp(LocalDateTime.now());

        statsRepository.save(hit1);
        statsRepository.save(hit2);
        statsRepository.save(hit3);

        List<StatsDto> stats = statsRepository.findAllByUriInAndTimestampBetween(LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(2), List.of("/test/1", "/test/2"), false);

        assertEquals(stats.size(), 2);
        assertEquals(stats.get(0).getUri(), "/test/1");
        assertEquals(stats.get(0).getHits(), 2);
        assertEquals(stats.get(1).getUri(), "/test/2");
    }
}