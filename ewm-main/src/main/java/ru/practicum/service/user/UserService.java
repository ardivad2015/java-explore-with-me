package ru.practicum.service.user;


import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {

    UserDto addNew(UserDto user);

    UserDto getById(Long id);

    List<UserDto> getAllByIds(List<Long> ids, Integer from, Integer size);

    void delete(Long id);

    void existsById(Long id);
}
