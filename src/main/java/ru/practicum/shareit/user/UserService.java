package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto user);

    UserDto update(UserDto user);

    UserDto getById(long id);

    void deleteUserById(long id);

    List<UserDto> getAll();
}
