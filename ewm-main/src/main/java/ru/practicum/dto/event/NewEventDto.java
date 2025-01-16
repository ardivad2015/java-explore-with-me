package ru.practicum.dto.event;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.model.Location;

import java.time.LocalDateTime;

@Getter
@Setter
public class NewEventDto {

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;
    @JsonProperty("category")
    @NotNull
    @Positive
    private Long categoryId;
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @Embedded
    private Location location;
    private boolean paid = false;
    @PositiveOrZero
    private Integer participantLimit = 0;
    private Boolean requestModeration = true;
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
    @Positive
    @JsonProperty("venue")
    private Long venueId;
}

