package ru.practicum.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.eventrequest.EventRequestStatusUpdateRequest;
import ru.practicum.dto.eventrequest.EventRequestStatusUpdateResult;
import ru.practicum.dto.eventrequest.EventRequestDto;
import ru.practicum.service.event.EventService;
import ru.practicum.service.eventrequest.EventRequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class PrivateEventController {

    private final EventService eventService;
    private final EventRequestService eventRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addNewEvent(@Positive @PathVariable("userId") Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        return eventService.addNew(userId, newEventDto);
    }

    @GetMapping("{eventId}")
    public EventFullDto getById(@Positive @PathVariable("userId") Long userId,
                                @Positive @PathVariable("eventId") Long eventId) {
        return eventService.getByIdFromPrivate(userId, eventId);
    }

    @GetMapping
    public List<EventShortDto> getByInitiator(@Positive @PathVariable("userId") Long userId,
                                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Positive @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getAllByInitiator(userId, from, size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@Positive @PathVariable Long userId,
                                    @Positive @PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return eventService.updateEventFromPrivate(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("{eventId}/requests")
    public List<EventRequestDto> getRequests(@Positive @PathVariable("userId") Long userId,
                                             @Positive @PathVariable("eventId") Long eventId) {
        return eventService.getAllRequestsByEventIdFromPrivate(userId, eventId);
    }

    @PatchMapping("{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatuses(@Positive @PathVariable("userId") Long userId,
                                                           @Positive @PathVariable("eventId") Long eventId,
                                                           @Valid @RequestBody EventRequestStatusUpdateRequest
                                                                   eventRequestStatusUpdateRequest) {
        return eventService.updateRequestsStatuses(userId, eventId, eventRequestStatusUpdateRequest);
    }

    @GetMapping("/location")
    public float getByInitiator(@RequestParam float lat1, @RequestParam float lon1,
                                @RequestParam float lat2, @RequestParam float lon2) {
        return eventService.calcDistance(lat1, lon1, lat2, lon2);
    }
}
