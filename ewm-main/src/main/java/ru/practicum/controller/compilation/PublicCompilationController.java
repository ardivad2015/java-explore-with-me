package ru.practicum.controller.compilation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.CompilationSearchDto;
import ru.practicum.service.compilation.CompilationService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
public class PublicCompilationController {

    private final CompilationService compilationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getCompilations(@RequestParam(required = false) boolean pinned,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                @Positive @RequestParam(defaultValue = "10") Integer size) {
        CompilationSearchDto searchDto = new CompilationSearchDto();
        searchDto.setPinned(pinned);
        searchDto.setFrom(from);
        searchDto.setSize(size);

        return compilationService.getAll(searchDto);
    }

    @GetMapping("/{compId}")
    public CompilationDto getById(@NotNull @PositiveOrZero @PathVariable Long compId) {
        return compilationService.getById(compId);
    }
}