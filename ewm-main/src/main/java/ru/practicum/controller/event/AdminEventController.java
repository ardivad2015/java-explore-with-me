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
import ru.practicum.model.EventState;
import ru.practicum.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
public class AdminEventController {

    private final EventService eventService;

    @GetMapping("{eventId}")
    public EventFullDto getById(@Positive @PathVariable("userId") Long userId,
                                @Positive @PathVariable("eventId") Long eventId) {
        return eventService.getByIdToUser(userId, eventId);
    }

    @GetMapping
    public List<EventShortDto> getAll(@RequestParam(value = "users", required = false) List<Long> usersIds,
                                      @RequestParam(required = false) List<EventState> states,
                                      @RequestParam(value = "categories", required = false) List<Long> categoriesIds,
                                      @RequestParam(required = false) LocalDateTime rangeStart,
                                      @RequestParam(required = false) LocalDateTime rangeEnd,
                                      @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                      @Positive @RequestParam(defaultValue = "10") int size) {
        return eventService.getAllToAdmin(usersIds, states, categoriesIds, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@Positive @PathVariable Long userId,
                                    @Positive @PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return eventService.updateEventByUser(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/location")
    public float getByInitiator(@RequestParam float lat1, @RequestParam float lon1,
                                              @RequestParam float lat2, @RequestParam float lon2) {
        return eventService.calcDistance(lat1, lon1, lat2, lon2);
    }
}
