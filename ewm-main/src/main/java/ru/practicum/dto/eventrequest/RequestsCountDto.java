package ru.practicum.dto.eventrequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class RequestsCountDto {

    private Long eventId;
    private Long requestsCount;
}
