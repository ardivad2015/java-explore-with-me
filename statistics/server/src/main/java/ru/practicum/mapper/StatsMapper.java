package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.StatsHitDto;
import ru.practicum.model.Hit;

@Mapper(componentModel = "spring")
public interface StatsMapper {
    Hit toStatistic(StatsHitDto statsHitDto);
}