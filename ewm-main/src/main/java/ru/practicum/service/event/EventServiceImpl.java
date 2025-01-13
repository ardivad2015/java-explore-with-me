package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.service.eventrequest.EventRequestService;
import ru.practicum.service.statistic.StatisticService;
import ru.practicum.util.ErrorMessage;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final StatisticService statisticService;
    private final EventRequestRepository requestRepository;
    private final EventRequestService eventRequestService;
    private final EventRequestMapper requestMapper;

    @Override
    @Transactional
    public EventFullDto addNew(Long userId, NewEventDto newEventDto) {
        final LocalDateTime createdOn = LocalDateTime.now();

        verifyEventDate(newEventDto.getEventDate(), createdOn);

        final User initiator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(ErrorMessage.userNotFoundMessage(userId)));
        final Category category = categoryRepository.findById(newEventDto.getCategoryId()).orElseThrow(() ->
                new NotFoundException(ErrorMessage.categoryNotFoundMessage(newEventDto.getCategoryId())));

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
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getByIdFromPublic(Long eventId, HttpServletRequest httpServletRequest) {
        final Event event = findById(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Событие недоступно");
        }
        statisticService.saveEndpointHit(httpServletRequest);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByInitiator(Long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(ErrorMessage.userNotFoundMessage(userId)));
        final EventSearchDto searchDto = EventSearchDto.builder()
                .usersIds(List.of(userId))
                .pageable(true)
                .pageInQuery(true)
                .sortInQuery(true)
                .from(from)
                .size(size).build();
        final List<Event> events = findAllBySearchRequest(searchDto);

        return events.stream()
                .map(eventMapper::toEventShortDto)
                .toList();
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
                    new NotFoundException(ErrorMessage.categoryNotFoundMessage(updateEventDto.getCategoryId()))));
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
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventFromAdmin(Long eventId, UpdateEventAdminRequest updateEventDto) {
        final Event event = findById(eventId);

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictPropertyConstraintException("Событие уже опубликовано.");
        }

        Optional.ofNullable(updateEventDto.getStateAction()).ifPresent(stateAction -> {
            if (stateAction.equals(StateAction.PUBLISH_EVENT)) {
                if (!event.getState().equals(EventState.PENDING)) {
                    throw new ConflictPropertyConstraintException("Событие не в статусе ожидания публикации.");
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (stateAction.equals(StateAction.REJECT_EVENT)) {
                event.setState(EventState.CANCELED);
            }
        });

        verifyEventDate(updateEventDto.getEventDate(), LocalDateTime.now());

        if (Objects.nonNull(updateEventDto.getCategoryId())) {
            event.setCategory(categoryRepository.findById(updateEventDto.getCategoryId()).orElseThrow(() ->
                    new NotFoundException(ErrorMessage.categoryNotFoundMessage(updateEventDto.getCategoryId()))));
        }

        Optional.ofNullable(updateEventDto.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEventDto.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEventDto.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateEventDto.getLocation()).ifPresent(event::setLocation);
        Optional.ofNullable(updateEventDto.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEventDto.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEventDto.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(updateEventDto.getTitle()).ifPresent(event::setTitle);

        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllFromAdmin(EventSearchDto eventSearchDto) {
        final List<Event> events = findAllBySearchRequest(eventSearchDto);
        return events.stream()
                .map(eventMapper::toEventFullDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllFromPublic(EventSearchDto eventSearchDto, HttpServletRequest httpServletRequest) {
        if (Objects.nonNull(eventSearchDto.getRangeStart()) && Objects.nonNull(eventSearchDto.getRangeEnd()) &&
                eventSearchDto.getRangeStart().isAfter(eventSearchDto.getRangeEnd())) {
            throw new BadRequestException("Дата начала не должна быть позже даты окончания");
        }
        statisticService.saveEndpointHit(httpServletRequest);
        eventSearchDto.setStates(List.of(EventState.PUBLISHED));

        final List<Event> events = findAllBySearchRequest(eventSearchDto);
        return events.stream()
                .map(eventMapper::toEventShortDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventRequestDto> getAllRequestsByEventIdFromPrivate(Long userId, Long eventId) {
        getUsersEvent(userId, eventId);
        return requestRepository.findAllByEventIdOrderByCreatedAsc(eventId).stream()
                .map(requestMapper::toEventRequestDto)
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
            updateResult.setRejectedRequests(requestsToUpdate.stream()
                    .peek(request -> request.setStatus(RequestStatus.REJECTED))
                    .map(requestMapper::toEventRequestDto)
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
            updateResult.setConfirmedRequests(requestsToUpdate.stream()
                    .peek(request -> request.setStatus(RequestStatus.CONFIRMED))
                    .map(requestMapper::toEventRequestDto)
                    .toList());
            return updateResult;
        }

        final int confirmationLimit = event.getParticipantLimit() - participantsCount;
        //Ограничиваем список остатком свободных мест, предварительно сортируя по дате заявки.
        final List<EventRequestDto> requestsToConfirm = requestsToUpdate.stream()
                .sorted(Comparator.comparing(EventRequest::getCreated))
                .limit(confirmationLimit)
                .peek(request -> request.setStatus(RequestStatus.CONFIRMED))
                .map(requestMapper::toEventRequestDto)
                .toList();
        final List<Long> idsToConfirm = requestsToConfirm.stream()
                .map(EventRequestDto::getId)
                .toList();

        requestRepository.updateStatusesByIds(idsToConfirm, RequestStatus.CONFIRMED);
        updateResult.setConfirmedRequests(requestsToConfirm);
        //если количество свободных мест было больше, чем размер списка, то больше ничего не надо.
        if (confirmationLimit > requestsToUpdate.size()) {
            return updateResult;
        }

        //Иначе, все свободные места теперь заняты, остальные заявки (видимо, не только из списка, а вообще из базы)
        // можно отклонить
        final List<EventRequest> requestsToReject = requestRepository.findAllByEventIdAndStatus(eventId,
                RequestStatus.PENDING);
        final List<Long> idsToReject = requestsToReject.stream()
                .map(EventRequest::getId)
                .toList();

        requestRepository.updateStatusesByIds(idsToReject, RequestStatus.REJECTED);
        updateResult.setRejectedRequests(requestsToReject.stream()
                .peek(request -> request.setStatus(RequestStatus.REJECTED))
                .map(requestMapper::toEventRequestDto)
                .toList());

        return updateResult;
    }

    @Override
    @Transactional(readOnly = true)
    public void addAdditionalInfo(Event event) {
        setAdditionalInfo(List.of(event));
    }

    @Override
    @Transactional(readOnly = true)
    public void addAdditionalInfo(Collection<Event> events) {
        setAdditionalInfo(events);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getAllByIds(List<Long> eventIds) {
        final EventSearchDto searchDto = EventSearchDto.builder()
                .ids(eventIds)
                .pageable(false).build();

        return findAllBySearchRequest(searchDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Event getById(Long eventId) {
        return findById(eventId);
    }

    private Event getUsersEvent(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(ErrorMessage.userNotFoundMessage(userId)));
        final Event event = findById(eventId);

        if (!userId.equals(event.getInitiator().getId())) {
            throw new BadRequestException("Пользователь не является инициатором события");
        }
        return event;
    }

    private Event findById(Long eventId) {
        final Event event = Optional.ofNullable(eventRepository.findByIdWithNestedEntitiesEagerly(eventId))
                .orElseThrow(() -> new NotFoundException(ErrorMessage.eventNotFoundMessage(eventId)));
        setAdditionalInfo(List.of(event));
        return event;
    }

    private void setAdditionalInfo(Collection<Event> events) {
        final Map<Long, Long> views = statisticService.getStatsByEvents(events);
        final Map<Long, Long> confirmedRequests = eventRequestService.countConfirmedRequestByEvents(events);

        events.forEach(event -> {
            event.setViews(views.getOrDefault(event.getId(), 0L));
            event.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0L));
        });
    }

    private List<Event> findAllBySearchRequest(EventSearchDto searchDto) {
        final boolean sortByViews = SortType.VIEWS.equals(searchDto.getSort());
        final boolean processingIsNeeded = sortByViews || searchDto.isOnlyAvailable();

        if (sortByViews) {
            searchDto.setSortInQuery(false);
            searchDto.setPageInQuery(false);
        }
        if (searchDto.isOnlyAvailable()) {
            searchDto.setPageInQuery(false);
        }
        List<Event> events = eventRepository.findAllBySearchRequest(searchDto);
        setAdditionalInfo(events);
        //если нет условий по подгружаемым полям, то ничего больше делать не надо
        if (!processingIsNeeded) {
            return events;
        }

        Stream<Event> eventStream = events.stream();
        if (searchDto.isOnlyAvailable()) {
            eventStream = eventStream.filter(event ->
                    event.getParticipantLimit() == 0 || event.getParticipantLimit() > event.getConfirmedRequests());
        }
        if (sortByViews) {
            eventStream = eventStream.sorted(Comparator.comparing(Event::getViews).reversed());
        }
        if (searchDto.isPageable()) {
            eventStream = eventStream.skip((long) searchDto.getFrom() / searchDto.getSize())
                    .limit(searchDto.getSize());
        }

        return eventStream.toList();
    }

    private List<EventRequest> getRequestsToUpdate(Long eventId, List<Long> requestsIds) {
        final Map<Long, EventRequest> requestsForUpdate = requestRepository.findAllById(requestsIds).stream()
                .collect(Collectors.toMap(EventRequest::getId, Function.identity()));

        for (Long requestId : requestsIds) {
            EventRequest requestForUpdate = requestsForUpdate.get(requestId);
            if (Objects.isNull(requestForUpdate)) {
                throw new NotFoundException(ErrorMessage.eventRequestNotFoundMessage(requestId));
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
}
