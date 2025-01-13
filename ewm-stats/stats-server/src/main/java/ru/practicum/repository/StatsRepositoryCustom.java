package ru.practicum.repository;

import ru.practicum.dto.StatsDto;
import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepositoryCustom {

    List<StatsDto> findAllByUriInAndTimestampBetween(LocalDateTime start, LocalDateTime end,
                                                     List<String> uris, boolean unique);
}
