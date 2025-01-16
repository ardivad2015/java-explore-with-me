package ru.practicum.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserMapper;
import ru.practicum.dto.user.UserMapperImpl;
import ru.practicum.model.User;
import ru.practicum.repository.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Captor
    ArgumentCaptor<User> userArgumentCaptor;

    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    void getAllByIds_whenIdsIsDefined_ThenCalledFindAllById() {
        final UserService userService = new UserServiceImpl(userRepository, userMapper);
        final List<Long> ids = new ArrayList<>();
        ids.add(1L);
        final List<User> userList = new ArrayList<>();
        final int from = 2;
        final int size = 3;
        final Pageable pageable = PageRequest.of(from, size);
        final Page<User> users = new PageImpl<>(userList, pageable, from + size);

        when(userRepository.findAllByIdIn(eq(ids), any(Pageable.class)))
                .thenReturn(users);
        final List<UserDto> actualUsers = userService.getAllByIds(ids, from, size);

        verify(userRepository, Mockito.never()).findAll(any(Pageable.class));

    }

    @Test
    void getAllByIds_whenIdsIsNull_ThenCalledFindAll() {
        final UserService userService = new UserServiceImpl(userRepository, userMapper);
        final List<User> userList = new ArrayList<>();
        final int from = 2;
        final int size = 3;
        final Pageable pageable = PageRequest.of(from, size);
        final Page<User> users = new PageImpl<>(userList, pageable, from + size);

        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(users);
        final List<UserDto> actualUsers = userService.getAllByIds(null, from, size);

        verify(userRepository, Mockito.never()).findAllByIdIn(eq(null), any(Pageable.class));

    }
}