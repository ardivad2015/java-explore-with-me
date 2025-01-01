package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatsHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class StatisticsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public void addStats(@Valid @RequestBody StatsHitDto statsHitDto) {
        statsService.save(statsHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam("start") LocalDateTime start,
                                       @RequestParam("end") LocalDateTime end,
                                       @RequestParam(value = "uris", required = false) List<String> uris,
                                       @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        return statsService.getStats(start, end, uris, unique);
    }
}
