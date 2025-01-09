package ru.practicum.dto.eventrequest;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.model.RequestStatus;

import java.util.List;

@Getter
@Setter
public class EventRequestStatusUpdateRequest {

    @NotNull
    private List<Long> requestIds;
    @NotNull
    private RequestStatus status;
}
