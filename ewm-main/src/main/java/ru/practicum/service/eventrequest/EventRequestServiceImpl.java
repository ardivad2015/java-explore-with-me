package ru.practicum.service.eventrequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.eventrequest.EventRequestDto;
import ru.practicum.dto.eventrequest.EventRequestMapper;
import ru.practicum.dto.eventrequest.RequestsCountDto;
import ru.practicum.exception.ConflictPropertyConstraintException;
import ru.practicum.exception.ConflictRelationsConstraintException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.*;
import ru.practicum.repository.event.EventRepository;
import ru.practicum.repository.eventrequest.EventRequestRepository;
import ru.practicum.repository.user.UserRepository;
import ru.practicum.util.ErrorMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventRequestServiceImpl implements EventRequestService {

    private final EventRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventRequestMapper requestMapper;

    @Override
    @Transactional
    public EventRequestDto addNew(Long userId, Long eventId) {
        final User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(ErrorMessage.userNotFoundMessage(userId)));
        final Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(ErrorMessage.eventNotFoundMessage(eventId)));

        if (requestRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new ConflictRelationsConstraintException("Запрос на участие в этом событии уже был отправлен");
        }

        if (event.getInitiator().equals(user)) {
            throw new ConflictPropertyConstraintException("Инициатор события не может добавить запрос на участие в " +
                    "своём событии");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictPropertyConstraintException("Событие ещё не опубликовано");
        }

        final int participantsCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= (participantsCount)) {
            throw new ConflictPropertyConstraintException("Достигнут лимит запросов на участие в событии");
        }

        final RequestStatus status = (!event.getRequestModeration() || event.getParticipantLimit() == 0) ?
                RequestStatus.CONFIRMED : RequestStatus.PENDING;

        EventRequest eventRequest = new EventRequest();
        eventRequest.setCreated(LocalDateTime.now().withNano(0));
        eventRequest.setUser(user);
        eventRequest.setEvent(event);
        eventRequest.setStatus(status);

        return requestMapper.toEventRequestDto(requestRepository.save(eventRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventRequestDto> getAllByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(ErrorMessage.userNotFoundMessage(userId));
        }

        return requestRepository.findAllByUserIdOrderByCreatedAsc(userId).stream()
                .map(requestMapper::toEventRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public EventRequestDto cancelById(Long userId, Long requestId) {
        final User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(ErrorMessage.userNotFoundMessage(userId)));
        final EventRequest eventRequest = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(ErrorMessage.eventRequestNotFoundMessage(requestId)));

        if (!eventRequest.getUser().equals(user)) {
            throw new ConflictRelationsConstraintException("Указана заявка от другого пользователя");
        }

        eventRequest.setStatus(RequestStatus.CANCELED);
        return requestMapper.toEventRequestDto(eventRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Long> countConfirmedRequestByEventIds(List<Long> eventIds) {
        return requestRepository.countRequestsByEventIdsAndStatus(
                eventIds, RequestStatus.CONFIRMED).stream()
                .collect(Collectors.toMap(RequestsCountDto::getEventId, RequestsCountDto::getRequestsCount));
    }
}
