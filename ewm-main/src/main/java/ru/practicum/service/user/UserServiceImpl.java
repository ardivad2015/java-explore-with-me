package ru.practicum.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserMapper;
import ru.practicum.exception.ConflictUniqueConstraintException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.User;
import ru.practicum.repository.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getById(Long id) {
        return userMapper.toUserDto(findById(id));
    }

    @Override
    public void existsById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
    }

    @Override
    @Transactional
    public UserDto addNew(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ConflictUniqueConstraintException(String.format("Email %s уже зарегистрирован ",
                    userDto.getEmail()));
        }
        final User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getAllByIds(List<Long> ids, Integer from, Integer size) {
        final Pageable pageable = PageRequest.of(0, size + from, Sort.by("id").ascending());
        Page<User> users;
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(pageable);
        } else {
            users = userRepository.findAllByIdIn(ids, pageable);
        }
        return users.stream()
                .skip(from)
                .map(userMapper::toUserDto)
                .toList();
    }

    private User findById(Long userId) {
       return userRepository.findById(userId).orElseThrow(() ->
               new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
   }
}