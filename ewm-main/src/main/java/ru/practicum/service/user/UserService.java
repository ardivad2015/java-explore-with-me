package ru.practicum.service.user;


import ru.practicum.dto.user.UserDto;
import ru.practicum.model.User;

import java.util.List;

public interface UserService {

    UserDto addNew(UserDto user);

    List<UserDto> getAllByIds(List<Long> ids, Integer from, Integer size);

    void delete(Long id);
}
