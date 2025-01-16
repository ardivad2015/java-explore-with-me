package ru.practicum.controller.venue;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.venue.VenueDto;
import ru.practicum.dto.venue.VenueUpdateDto;
import ru.practicum.service.venue.VenueService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/venues")
@Validated
public class AdminVenueController {

    private final VenueService venueService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VenueDto addNew(@Valid @RequestBody VenueDto venueDto) {
        return venueService.addNew(venueDto);
    }

    @PatchMapping("/{venueId}")
    public VenueDto update(@Positive @PathVariable Long venueId, @Valid @RequestBody VenueUpdateDto venueDto) {
        return venueService.update(venueId, venueDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<VenueDto> getAll(@RequestParam(required = false) List<Long> ids,
                                 @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                 @Positive @RequestParam(defaultValue = "10") int size) {
        return venueService.getAllByIds(ids, from, size);
    }

    @DeleteMapping("/{venueId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Positive @PathVariable Long venueId) {
        venueService.delete(venueId);
    }
}

