package ru.practicum.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilation.*;
import ru.practicum.exception.ConflictRelationsConstraintException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.compilation.CompilationRepository;
import ru.practicum.service.event.EventService;
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
    private final EventService eventService;
    private final StatisticService statisticService;
    private final EventRequestService eventRequestService;
    private final CompilationMapper compilationMapper;

    @Override
    @Transactional
    public CompilationDto addNew(NewCompilationDto newCompilationDto) {
        if (compilationRepository.existsByTitle(newCompilationDto.getTitle())) {
            throw new ConflictRelationsConstraintException(
                    ErrorMessage.compilationWithTitleExists(newCompilationDto.getTitle()));
        }

        final List<Long> eventIds = newCompilationDto.getEvents();
        final Compilation compilation = compilationMapper.toCompilation(newCompilationDto);

        compilation.setEvents(getCompilationEvents(eventIds));
        compilationRepository.save(compilation);
        return compilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationRequest updateRequest) {
        final Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(ErrorMessage.compilationNotFoundMessage(compId)));

        Optional.ofNullable(updateRequest.getTitle()).ifPresent(compilation::setTitle);
        Optional.ofNullable(updateRequest.getPinned()).ifPresent(compilation::setPinned);

        final List<Long> eventIds = updateRequest.getEvents();

        if (Objects.nonNull(eventIds)) {
            compilation.setEvents(getCompilationEvents(eventIds));
        }
        return compilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException(ErrorMessage.compilationNotFoundMessage(compId));
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAll(CompilationSearchDto searchDto) {
        final List<Compilation> compilations =
                compilationRepository.findAllBySearchRequestWithNestedEntitiesEagerly(searchDto);
        final Set<Event> events = compilations.stream()
                .filter(compilation -> Objects.nonNull(compilation.getEvents()))
                .flatMap(compilation -> compilation.getEvents().stream())
                .collect(Collectors.toSet());
        eventService.addAdditionalInfo(events);
        return compilations.stream().map(compilationMapper::toCompilationDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getById(Long compId) {
        final Compilation compilation = compilationRepository.findByIdWithNestedEntitiesEagerly(compId);
        final Collection<Event> events = compilation.getEvents();

        if (Objects.nonNull(events)) {
            eventService.addAdditionalInfo(events);
        }
        return compilationMapper.toCompilationDto(compilation);
    }

    private Set<Event> getCompilationEvents(List<Long> eventIds) {
        if (Objects.isNull(eventIds)) {
            return Collections.emptySet();
        }

        final Map<Long, Event> events = eventService.getAllByIds(eventIds).stream()
                .collect(Collectors.toMap(Event::getId, Function.identity()));

        for (Long eventId : eventIds) {
            if (Objects.isNull(events.get(eventId))) {
                throw new NotFoundException(ErrorMessage.eventNotFoundMessage(eventId));
            }
        }
        return new HashSet<>(events.values());
    }
}
