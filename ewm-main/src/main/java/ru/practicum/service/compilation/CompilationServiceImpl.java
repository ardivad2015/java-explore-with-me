package ru.practicum.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.compilation.*;
import ru.practicum.exception.ConflictRelationsConstraintException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.compilation.CompilationRepository;
import ru.practicum.repository.event.EventRepository;
import ru.practicum.service.event.util.EventProcessing;
import ru.practicum.service.eventrequest.EventRequestService;
import ru.practicum.service.statistic.StatisticService;
import ru.practicum.util.ErrorMessage;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final StatisticService statisticService;
    private final EventRequestService eventRequestService;
    private final CompilationMapper compilationMapper;

    @Override
    public CompilationDto addNew(NewCompilationDto newCompilationDto) {
        if (compilationRepository.existsByTitle(newCompilationDto.getTitle())) {
            throw new ConflictRelationsConstraintException(
                    ErrorMessage.compilationWithTitleExists(newCompilationDto.getTitle()));
        }

        final List<Long> eventIdsToAdd = newCompilationDto.getEvents();
        final Compilation compilation = compilationMapper.toCompilation(newCompilationDto);

        setEventsToCompilation(compilation, eventIdsToAdd);

        compilationRepository.save(compilation);
        return compilationMapper.toCompilationDto(compilation);
    }

    @Override
    public CompilationDto update(Long compId, UpdateCompilationRequest updateRequest) {
        final Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(ErrorMessage.compilationNotFoundMessage(compId)));

        Optional.ofNullable(updateRequest.getTitle()).ifPresent(compilation::setTitle);
        Optional.ofNullable(updateRequest.getPinned()).ifPresent(compilation::setPinned);

        final List<Long> eventIdsToAdd = updateRequest.getEvents();

        setEventsToCompilation(compilation, eventIdsToAdd);

        return compilationMapper.toCompilationDto(compilation);
    }

    @Override
    public void delete(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException(ErrorMessage.compilationNotFoundMessage(compId));
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getAll(CompilationSearchDto searchDto) {
        final List<Compilation> compilations =
                compilationRepository.findAllBySearchRequestWithNestedEntitiesEagerly(searchDto);

    }

    @Override
    public CompilationDto getById(Long compId) {
        final Compilation compilation = compilationRepository.findByIdWithNestedEntitiesEagerly(compId);
        final Collection<Event> events = compilation.getEvents();

        if (Objects.nonNull(events)) {
            final List<Long> eventIds = compilation.getEvents().stream().map(Event::getId).toList();
            final Map<Long, Long> views = statisticService.getStatsByEvents(events);
            final Map<Long, Long> confirmedRequests = eventRequestService.countConfirmedRequestByEventIds(eventIds);

            events.forEach(event -> EventProcessing.fillAdditionalInfo(event, views, confirmedRequests));
        }

        return compilationMapper.toCompilationDto(compilation);
    }

    private void setEventsToCompilation(Compilation compilation, List<Long> eventIds) {
        if (Objects.isNull(eventIds)) {
            return;
        }

        final Map<Long, Event> eventsToAdd = eventRepository.findAllByIdInWithCategoryAndUserEagerly(eventIds).stream()
                .collect(Collectors.toMap(Event::getId, Function.identity()));

        for (Long eventId : eventIds) {
            if (Objects.isNull(eventsToAdd.get(eventId))) {
                throw new NotFoundException(ErrorMessage.eventNotFoundMessage(eventId));
            }
        }

        final Collection<Event> events = eventsToAdd.values();
        final Map<Long, Long> views = statisticService.getStatsByEvents(events);
        final Map<Long, Long> confirmedRequests = eventRequestService.countConfirmedRequestByEventIds(eventIds);

        events.forEach(event -> EventProcessing.fillAdditionalInfo(event, views, confirmedRequests));
        compilation.setEvents(new HashSet<>(events));
    }
}
