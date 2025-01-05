package ru.practicum.dto.event;

import lombok.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventShortDto {

    private Long id;
    private String annotation;
    private CategoryDto category;
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private boolean paid;
    private String title;
    private Integer confirmedRequests;
    private Long views;
}
