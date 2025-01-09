package ru.practicum.dto.eventrequest;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.model.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventRequestDto {

    private Long id;
    private LocalDateTime created;
    private Long event;
    private Long requester;
    private RequestStatus status;
}
