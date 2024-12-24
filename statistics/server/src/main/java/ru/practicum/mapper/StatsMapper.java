package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.StatsHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.model.Hit;

@Mapper(componentModel = "spring")
public interface StatsMapper {

    Hit toHit(StatsHitDto statsHitDto);

    ViewStatsDto toViewStatsDto(StatsDto statsDto);

}