package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.Hit;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsMapper statsMapper;
    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public void save(StatsHitDto statsHitDto) {
        Hit hit = statsMapper.toHit(statsHitDto);
        statsRepository.save(hit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
          return statsRepository.findAllByUriInAndTimestampBetween(start, end, uris, unique).stream()
                  .map(statsMapper::toViewStatsDto)
                  .toList();
    }
}
