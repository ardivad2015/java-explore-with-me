package ru.practicum.controller.partrequest;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.eventrequest.EventRequestDto;
import ru.practicum.service.eventrequest.EventRequestService;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
public class EventRequestController {

    private final EventRequestService eventRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventRequestDto addNewEvent(@Positive @PathVariable("userId") Long userId,
                                       @Positive @RequestParam("eventId") Long eventId) {
        return eventRequestService.addNew(userId, eventId);
    }
}
