package ru.practicum.dto.venue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.model.Location;

@Getter
@Setter
public class VenueDto {

    private Long id;
    @NotNull
    private Location location;
    @NotBlank
    @Size(max = 120, min = 3)
    private String name;
}