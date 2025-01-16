package ru.practicum.dto.venue;

import org.mapstruct.Mapper;
import ru.practicum.model.Venue;

@Mapper(componentModel = "spring")
public interface VenueMapper {

    VenueDto toVenueDto(Venue venue);

    Venue toVenue(VenueDto venueDto);
}
