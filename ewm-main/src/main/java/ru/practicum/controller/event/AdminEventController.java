package ru.practicum.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.*;
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

    @PatchMapping("{eventId}")
    public EventFullDto getById(@Positive @PathVariable("eventId") Long eventId,
                                @Valid @RequestBody UpdateEventAdminRequest updateRequest) {
        return eventService.updateEventFromAdmin(eventId, updateRequest);
    }

    @GetMapping
    public List<EventFullDto> getAll(@RequestParam(value = "users", required = false) List<Long> usersIds,
                                      @RequestParam(required = false) List<EventState> states,
                                      @RequestParam(value = "categories", required = false) List<Long> categoriesIds,
                                      @RequestParam(required = false) LocalDateTime rangeStart,
                                      @RequestParam(required = false) LocalDateTime rangeEnd,
                                      @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                      @Positive @RequestParam(defaultValue = "10") int size) {
        final EventAdminSearchDto eventSearchDto = EventAdminSearchDto.builder()
                .usersIds(usersIds)
                .states(states)
                .categoriesIds(categoriesIds)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();

        return eventService.getAllFromAdmin(eventSearchDto);
    }

    @GetMapping("/test")
    public void test() {
         eventService.test();
    }
}
