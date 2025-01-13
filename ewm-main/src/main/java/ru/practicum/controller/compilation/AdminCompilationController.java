package ru.practicum.controller.compilation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.service.compilation.CompilationService;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Validated
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return compilationService.addNew(newCompilationDto);
    }

  @DeleteMapping("/{compId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void remove(@Positive @PathVariable Long compId) {
       compilationService.delete(compId);
   }

    @PatchMapping("/{compId}")
    public CompilationDto update(@Positive @PathVariable Long compId,
                                 @Valid @RequestBody UpdateCompilationRequest updateRequest) {
        return compilationService.update(compId, updateRequest);
    }
}
