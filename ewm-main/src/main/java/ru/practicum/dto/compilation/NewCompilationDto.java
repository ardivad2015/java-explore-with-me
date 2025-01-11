package ru.practicum.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NewCompilationDto {

    private List<Long> events;
    private boolean pinned;
    @NotBlank
    @Size(max = 50, min = 1)
    private String title;
}
