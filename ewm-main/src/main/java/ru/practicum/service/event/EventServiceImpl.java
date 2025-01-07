package ru.practicum.service.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.deser.key.LocalDateKeyDeserializer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import ru.practicum.StatisticsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.dto.event.*;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictPropertyConstraintException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.User;
import ru.practicum.repository.category.CategoryRepository;
import ru.practicum.repository.event.EventRepository;
import ru.practicum.repository.user.UserRepository;
import ru.practicum.util.ErrorMessage;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final StatisticsClient statisticsClient;
    private final String GET_EVENT_ENDPOINT = "events/";

    @Override
    @Transactional
    public EventFullDto addNew(Long userId, NewEventDto newEventDto) {
        final LocalDateTime createdOn = LocalDateTime.now();

        verifyEventDate(newEventDto.getEventDate(), createdOn);

        final User initiator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(ErrorMessage.UserNotFoundMessage(userId)));
        final Category category = categoryRepository.findById(newEventDto.getCategoryId()).orElseThrow(() ->
                new NotFoundException(ErrorMessage.UserNotFoundMessage(userId)));

        final Event event = eventMapper.toEvent(newEventDto);
        event.setCategory(category);
        event.setInitiator(initiator);
        event.setCreatedOn(createdOn);
        event.setState(EventState.PENDING);
        eventRepository.save(event);

        final EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        eventFullDto.setConfirmedRequests(0);
        eventFullDto.setViews(0L);

        return eventFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getFullById(Long userId, Long eventId) {
        final Event event  = getUsersEvent(userId, eventId);
        final List<ViewStatsDto> views = getStatsByEventsIds(List.of(event));

        fillAdditionalInfo(event, views);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByInitiator(Long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(ErrorMessage.UserNotFoundMessage(userId)));
        final List<Event> events = eventRepository.findAllByInitiatorIdWithCategoryAndInitiator(userId, from, size);
        final List<ViewStatsDto> views = getStatsByEventsIds(events);

        return events.stream()
                .map(event -> {
                    fillAdditionalInfo(event, views);
                    return eventMapper.toEventShortDto(event);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        final Event event  = getUsersEvent(userId, eventId);

        event.setAnnotation(updateEventUserRequest.getAnnotation());
        return eventMapper.toEventFullDto(event);
    }

    @Transactional(readOnly = true)
    private Event getUsersEvent(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(ErrorMessage.UserNotFoundMessage(userId)));
        final Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(ErrorMessage.EventNotFoundMessage(eventId)));

        if (!userId.equals(event.getInitiator().getId())) {
            throw new BadRequestException("Пользователь не является инициатором события");
        }

        return event;
    }

    private void verifyEventDate(LocalDateTime eventDate, LocalDateTime startDate) {
        if (eventDate.withNano(0).isBefore(startDate.withNano(0).plusHours(2))) {
            throw new BadRequestException("Дата и время события не может быть раньше, " +
                    "чем через два часа от текущего момента");
        }
    }

    private List<ViewStatsDto> getStatsByEventsIds(Collection<Event> events) {

        final List<Event> publishedEvents = events.stream()
                .filter(event -> Objects.nonNull(event.getPublishedOn()))
                .sorted(Comparator.comparing(Event::getPublishedOn))
                .toList();

        if (publishedEvents.isEmpty()) {
            return Collections.emptyList();
        }

        final List<String> uris = publishedEvents.stream().map(event -> GET_EVENT_ENDPOINT + event.getId()).toList();
        final LocalDateTime start = publishedEvents.get(0).getPublishedOn();

        try {
            return statisticsClient.getStats(start, LocalDateTime.now(),
                    uris, true).getBody();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private void fillAdditionalInfo(Event event, List<ViewStatsDto> views) {
        event.setViews(views.stream()
                .filter(viewStatsDto -> viewStatsDto.getUri().equals(GET_EVENT_ENDPOINT + event.getId()))
                .mapToLong(ViewStatsDto::getHits).sum());
        event.setConfirmedRequests(0);
    }
}
