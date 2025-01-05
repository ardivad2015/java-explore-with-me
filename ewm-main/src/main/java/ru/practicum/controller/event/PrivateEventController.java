package ru.practicum.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.service.event.EventService;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class PrivateEventController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addNewEvent(@Positive @PathVariable("userId") Long userId,
                                 @Valid @RequestBody NewEventDto newEventDto) {
        return eventService.addNew(userId, newEventDto);
    }

    @GetMapping("{eventId}")
    public EventFullDto getById(@Positive @PathVariable("userId") Long userId,
                                @Positive @PathVariable("eventId") Long eventId) {
        return eventService.getFullById(userId, eventId);
    }
}
