package ru.practicum.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventMapper;
import ru.practicum.dto.event.NewEventDto;
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

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;

    @Override
    public EventFullDto addNew(Long userId, NewEventDto newEventDto) {
        final LocalDateTime createdOn = LocalDateTime.now();

        if (newEventDto.getEventDate().isBefore(createdOn.plusHours(2))) {
            throw new ConflictPropertyConstraintException("Дата и время события не может быть раньше, " +
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
        eventFullDto.setViews(0);

        return eventFullDto;
    }
}
