package ru.practicum.service.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import ru.practicum.StatisticsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventMapper;
import ru.practicum.dto.event.NewEventDto;
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
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final StatisticsClient statisticsClient;

    @Override
    @Transactional
    public EventFullDto addNew(Long userId, NewEventDto newEventDto) {
        final LocalDateTime createdOn = LocalDateTime.now();

        if (newEventDto.getEventDate().isBefore(createdOn.plusHours(2))) {
            throw new BadRequestException("Дата и время события не может быть раньше, " +
                    "чем через два часа от текущего момента");
        }

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
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(ErrorMessage.UserNotFoundMessage(userId)));
        final Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(ErrorMessage.EventNotFoundMessage(eventId)));

        if (!userId.equals(event.getInitiator().getId())) {
            throw new BadRequestException("Пользователь не является инициатором события");
        }

        final EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        eventFullDto.setConfirmedRequests(0);
        List<String> uris = List.of("events/" + eventId);
        Long viewsCount = 0L;
        try {
            List<ViewStatsDto> views = statisticsClient.getStats(event.getCreatedOn(), LocalDateTime.now(),
                    uris, true).getBody();
            if (Objects.nonNull(views) && views.size() == 1) {
              viewsCount = views.get(0).getHits();
            }
        } catch (RestClientException e) {

        }
        eventFullDto.setViews(viewsCount);
        return eventFullDto;
    }
}
