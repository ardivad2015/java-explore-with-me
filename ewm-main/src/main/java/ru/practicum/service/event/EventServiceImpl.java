package ru.practicum.service.event;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatisticsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.dto.event.*;
import ru.practicum.dto.eventrequest.*;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictPropertyConstraintException;
import ru.practicum.exception.ConflictRelationsConstraintException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.*;
import ru.practicum.repository.category.CategoryRepository;
import ru.practicum.repository.event.EventRepository;
import ru.practicum.repository.eventrequest.EventRequestRepository;
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
    private final EventRequestRepository requestRepository;
    private final EventRequestMapper requestMapper;
    private final String GET_EVENT_ENDPOINT = "events/";

    @Override
    @Transactional
    public EventFullDto addNew(Long userId, NewEventDto newEventDto) {
        final LocalDateTime createdOn = LocalDateTime.now();

        verifyEventDate(newEventDto.getEventDate(), createdOn);

        final User initiator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(ErrorMessage.UserNotFoundMessage(userId)));
        final Category category = categoryRepository.findById(newEventDto.getCategoryId()).orElseThrow(() ->
                new NotFoundException(ErrorMessage.CategoryNotFoundMessage(newEventDto.getCategoryId())));

        final Event event = eventMapper.toEvent(newEventDto);
        event.setCategory(category);
        event.setInitiator(initiator);
        event.setCreatedOn(createdOn);
        event.setState(EventState.PENDING);
        eventRepository.save(event);

        final EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        eventFullDto.setConfirmedRequests(0L);
        eventFullDto.setViews(0L);

        return eventFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getByIdFromPrivate(Long userId, Long eventId) {
        final Event event = getUsersEvent(userId, eventId);
        final List<ViewStatsDto> views = getStatsByEvents(List.of(event));
        final List<RequestsCountDto> confirmedRequests = requestRepository.countRequestsByEventsIdsAndStatus(
                List.of(eventId), RequestStatus.CONFIRMED);

        fillAdditionalInfo(event, views, confirmedRequests);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByInitiator(Long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(ErrorMessage.UserNotFoundMessage(userId)));
        final List<Event> events = eventRepository.findAllByInitiatorIdWithCategoryAndInitiator(userId, from, size);
        final List<ViewStatsDto> views = getStatsByEvents(events);
        final List<RequestsCountDto> confirmedRequests = requestRepository.countRequestsByEventsIdsAndStatus(
                events.stream().map(Event::getId).toList(), RequestStatus.CONFIRMED);

        return events.stream()
                .map(event -> {
                    fillAdditionalInfo(event, views, confirmedRequests);
                    return eventMapper.toEventShortDto(event);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventFromPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEventDto) {
        final Event event = getUsersEvent(userId, eventId);

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictPropertyConstraintException("Событие уже опубликовано.");
        }

        verifyEventDate(updateEventDto.getEventDate(), LocalDateTime.now());

        if (Objects.nonNull(updateEventDto.getCategoryId())) {
            event.setCategory(categoryRepository.findById(updateEventDto.getCategoryId()).orElseThrow(() ->
                    new NotFoundException(ErrorMessage.CategoryNotFoundMessage(updateEventDto.getCategoryId()))));
        }

        Optional.ofNullable(updateEventDto.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEventDto.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEventDto.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateEventDto.getLocation()).ifPresent(event::setLocation);
        Optional.ofNullable(updateEventDto.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEventDto.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEventDto.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(updateEventDto.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(updateEventDto.getStateAction()).ifPresent(stateAction -> {
            if (stateAction.equals(StateAction.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else if (stateAction.equals(StateAction.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
            }
        });

        final List<ViewStatsDto> views = getStatsByEvents(List.of(event));
        final List<RequestsCountDto> confirmedRequests = requestRepository.countRequestsByEventsIdsAndStatus(
                List.of(eventId), RequestStatus.CONFIRMED);
        fillAdditionalInfo(event, views, confirmedRequests);

        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventFromAdmin(Long eventId, UpdateEventAdminRequest updateEventDto) {
        final Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(ErrorMessage.EventNotFoundMessage(eventId)));

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictPropertyConstraintException("Событие уже опубликовано.");
        }

        Optional.ofNullable(updateEventDto.getStateAction()).ifPresent(stateAction -> {
                    if (stateAction.equals(StateAction.PUBLISH_EVENT)) {
                        if (!event.getState().equals(EventState.PENDING)) {
                            throw new ConflictPropertyConstraintException("Событие не в статусе ожидания публикации.");
                        }
                        event.setState(EventState.PUBLISHED);
                    } else if (stateAction.equals(StateAction.REJECT_EVENT)) {
                        event.setState(EventState.CANCELED);
                    }
                });

        verifyEventDate(updateEventDto.getEventDate(), LocalDateTime.now());

        if (Objects.nonNull(updateEventDto.getCategoryId())) {
            event.setCategory(categoryRepository.findById(updateEventDto.getCategoryId()).orElseThrow(() ->
                    new NotFoundException(ErrorMessage.CategoryNotFoundMessage(updateEventDto.getCategoryId()))));
        }

        Optional.ofNullable(updateEventDto.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEventDto.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEventDto.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateEventDto.getLocation()).ifPresent(event::setLocation);
        Optional.ofNullable(updateEventDto.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEventDto.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEventDto.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(updateEventDto.getTitle()).ifPresent(event::setTitle);

        final List<ViewStatsDto> views = getStatsByEvents(List.of(event));
        final List<RequestsCountDto> confirmedRequests = requestRepository.countRequestsByEventsIdsAndStatus(
                List.of(eventId), RequestStatus.CONFIRMED);
        fillAdditionalInfo(event, views, confirmedRequests);

        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllFromAdmin(EventAdminSearchDto eventSearchDto) {
        final List<Event> events = eventRepository.getAllBySearchRequest(eventSearchDto);
        final List<ViewStatsDto> views = getStatsByEvents(events);
        final List<RequestsCountDto> confirmedRequests = requestRepository.countRequestsByEventsIdsAndStatus(
        events.stream().map(Event::getId).toList(), RequestStatus.CONFIRMED);

        return events.stream()
                .map(event -> {
                    fillAdditionalInfo(event, views, confirmedRequests);
                    return eventMapper.toEventFullDto(event);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventRequestDto> getAllRequestsByEventIdFromPrivate(Long userId, Long eventId) {
        final Event event = getUsersEvent(userId, eventId);
        return requestRepository.findAllByEventIdOrderByCreatedAsc(eventId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestsStatuses(Long userId, Long eventId,
                                                                 EventRequestStatusUpdateRequest
                                                                         statusUpdateRequest) {
        final Event event = getUsersEvent(userId, eventId);
        final EventRequestStatusUpdateResult updateResult = new EventRequestStatusUpdateResult();

        if (!event.getRequestModeration()) {
            return updateResult;
        }

        final List<EventRequest> requestsToUpdate = getRequestsToUpdate(eventId, statusUpdateRequest.getRequestIds());
        //Если надо отклонить, то проверки на лимит участников не нужны.
        if (statusUpdateRequest.getStatus().equals(RequestStatus.REJECTED)) {
            requestRepository.updateStatusesByIds(statusUpdateRequest.getRequestIds(), RequestStatus.REJECTED);
            updateResult.setRejectedRequests(
                    requestRepository.findAllByIdInOrderByCreatedAsc(statusUpdateRequest.getRequestIds()).stream()
                            .map(requestMapper::toParticipationRequestDto)
                            .toList());
            return updateResult;
        }

        final int participantsCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= participantsCount) {
            throw new ConflictPropertyConstraintException("Достигнут лимит запросов на участие в событии");
        }
        //Если контроля лимита нет, то просто меняем статусы у всего списка.
        if (event.getParticipantLimit() == 0) {
            requestRepository.updateStatusesByIds(statusUpdateRequest.getRequestIds(), RequestStatus.CONFIRMED);
            updateResult.setConfirmedRequests(
                    requestRepository.findAllByIdInOrderByCreatedAsc(statusUpdateRequest.getRequestIds()).stream()
                            .map(requestMapper::toParticipationRequestDto)
                            .toList());
            return updateResult;
        }

        final int confirmationLimit = event.getParticipantLimit() - participantsCount;
        //Ограничиваем список остатком свободных мест, предварительно сортируя по дате заявки.
        final List<Long> idsToConfirm = requestsToUpdate.stream()
                .sorted(Comparator.comparing(EventRequest::getCreated))
                .limit(confirmationLimit)
                .map(EventRequest::getId)
                .toList();

        requestRepository.updateStatusesByIds(idsToConfirm, RequestStatus.CONFIRMED);

        updateResult.setConfirmedRequests(requestRepository.findAllByIdInOrderByCreatedAsc(idsToConfirm).stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList());
        //если количество свободных мест было больше, чем размер списка, то больше ничего не надо.
        if (confirmationLimit > requestsToUpdate.size()) {
            return updateResult;
        }

        //Иначе, все свободные места теперь заняты, остальные заявки (видимо, не только из списка, а вообще из базы)
        // можно отклонить
        final List<Long> idsToReject = requestRepository.findAllByEventIdAndStatus(eventId,
                        RequestStatus.PENDING).stream()
                .map(EventRequest::getId)
                .toList();

        requestRepository.updateStatusesByIds(idsToReject, RequestStatus.REJECTED);

        updateResult.setRejectedRequests(requestRepository.findAllByIdInOrderByCreatedAsc(idsToReject).stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList());

        return updateResult;
    }

    @Override
    @Transactional
    public void test() {
        final Long eventId = 1L;
        final Long userId = 1L;
        final List<Long> requestsIds = List.of(3L, 4L,5L, 6L, 7L, 8L);
        final Event event = getUsersEvent(userId, eventId);

        final List<EventRequest> requestsToUpdate = getRequestsToUpdate(eventId, requestsIds);

        final int participantsCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        final int confirmationLimit = event.getParticipantLimit() - participantsCount;
        //Ограничиваем список остатком свободных мест, предварительно сортируя по дате заявки.
        final List<EventRequest> requestsToConfirm = requestsToUpdate.stream()
               .sorted(Comparator.comparing(EventRequest::getCreated))
              .limit(confirmationLimit)
              .peek(request -> request.setStatus(RequestStatus.CONFIRMED))
              .toList();
        //requestRepository.saveAll(requestsToUpdate);
    }

    @Override
    public float calcDistance(float lat1, float lon1, float lat2, float lon2) {
        return eventRepository.distance(lat1, lon1, lat2, lon2);
    }

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

    private List<EventRequest> getRequestsToUpdate(Long eventId, List<Long> requestsIds) {
        final Map<Long, EventRequest> requestsForUpdate = requestRepository.findAllById(requestsIds).stream()
                .collect(Collectors.toMap(EventRequest::getId, Function.identity()));

        for (Long requestId : requestsIds) {
            EventRequest requestForUpdate = requestsForUpdate.get(requestId);
            if (Objects.isNull(requestForUpdate)) {
                throw new NotFoundException(ErrorMessage.EventRequestNotFoundMessage(requestId));
            }

            if (!requestForUpdate.getEvent().getId().equals(eventId)) {
                throw new ConflictRelationsConstraintException(String.format("Запрос с id = %d принадлежит другому " +
                        "событию", requestId));
            }

            if (!requestForUpdate.getStatus().equals(RequestStatus.PENDING)) {
                throw new ConflictPropertyConstraintException(String.format("Запрос с id = %d не в статусе ожидания",
                        requestId));
            }
        }

        return requestsForUpdate.values().stream().toList();
    }

    private void verifyEventDate(LocalDateTime eventDate, LocalDateTime startDate) {
        if (Objects.nonNull(eventDate) && eventDate.withNano(0).isBefore(startDate.withNano(0)
                .plusHours(2))) {
            throw new BadRequestException("Дата и время события не может быть раньше, " +
                    "чем через два часа от текущего момента");
        }
    }

    private List<ViewStatsDto> getStatsByEvents(Collection<Event> events) {

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

    private void fillAdditionalInfo(Event event, List<ViewStatsDto> views, List<RequestsCountDto> confirmedRequests) {
        event.setViews(views.stream()
                .filter(viewStatsDto -> viewStatsDto.getUri().equals(GET_EVENT_ENDPOINT + event.getId()))
                .mapToLong(ViewStatsDto::getHits).sum());
        event.setConfirmedRequests(confirmedRequests.stream()
                .filter(requestsCountDto -> requestsCountDto.getEventId().equals(event.getId()))
                .mapToLong(RequestsCountDto::getRequestsCount).sum());
    }
}
