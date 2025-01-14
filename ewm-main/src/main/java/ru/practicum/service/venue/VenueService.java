package ru.practicum.service.venue;

import ru.practicum.dto.venue.VenueDto;
import ru.practicum.dto.venue.VenueUpdateDto;

import java.util.List;

public interface VenueService {

    VenueDto addNew(VenueDto venueDto);

    VenueDto update(Long venueId, VenueUpdateDto venueDto);

    List<VenueDto> getAllByIds(List<Long> ids, Integer from, Integer size);

    void delete(Long id);
}
