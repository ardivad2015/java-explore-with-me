package ru.practicum.service.venue;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.venue.VenueDto;
import ru.practicum.dto.venue.VenueMapper;
import ru.practicum.dto.venue.VenueUpdateDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Venue;
import ru.practicum.repository.venue.VenueRepository;
import ru.practicum.util.ErrorMessage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;
    private final VenueMapper venueMapper;

    @Override
    @Transactional
    public VenueDto addNew(VenueDto venueDto) {
        final Venue venue = venueMapper.toVenue(venueDto);
        return venueMapper.toVenueDto(venueRepository.save(venue));
    }

    @Override
    @Transactional
    public VenueDto update(Long venueId, VenueUpdateDto venueDto) {
        final Venue venue = venueRepository.findById(venueId).orElseThrow(() ->
                new NotFoundException(ErrorMessage.venueNotFoundMessage(venueId)));

        Optional.ofNullable(venueDto.getLocation()).ifPresent(venue::setLocation);
        Optional.ofNullable(venueDto.getName()).ifPresent(venue::setName);
        return venueMapper.toVenueDto(venue);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        venueRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VenueDto> getAllByIds(List<Long> ids, Integer from, Integer size) {
        final Pageable pageable = PageRequest.of(from, size, Sort.by("id").ascending());
        Page<Venue> venues;
        if (ids == null || ids.isEmpty()) {
            venues = venueRepository.findAll(pageable);
        } else {
            venues = venueRepository.findAllByIdIn(ids, pageable);
        }
        return venues.stream()
                .map(venueMapper::toVenueDto)
                .toList();
    }
}
