package ru.practicum.dto.compilation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompilationSearchDto {

    private Boolean pinned;
    private int from;
    private int size;
}
