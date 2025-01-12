package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.EventState;
import ru.practicum.model.Location;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class EventFullDto {

    private Long id;
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private EventState state;
    private String title;
    private Long views;
}
