package ru.practicum.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatsHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatisticsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public void addStats(@Valid @RequestBody StatsHitDto statsHitDto) {
        statsService.save(statsHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> viewStats(@RequestParam("start")
                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                       @RequestParam("end")
                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                       @RequestParam(value = "uris", required = false) List<String> uris,
                                       @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        return statsService.getStats(start, end, uris, unique);
    }
}
