package ru.practicum.repository;

import ru.practicum.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepositoryCustom {

    List<ViewStatsDto> findAllByUriInAndTimestampBetween(LocalDateTime start, LocalDateTime end,
                                                         List<String> uris, boolean unique);
}
