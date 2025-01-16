package ru.practicum.dto.venue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.model.Location;

@Getter
@Setter
public class VenueUpdateDto {

    private Long id;
    private Location location;
    @NotBlank
    @Size(max = 120, min = 3)
    private String name;
}