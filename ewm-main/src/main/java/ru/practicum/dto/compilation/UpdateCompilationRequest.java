package ru.practicum.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
public class UpdateCompilationRequest {

    private List<Long> events;
    private Boolean pinned;
    @Size(max = 50, min = 1)
    private String title;
}
