package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) {
        return userMapper.toUserDto(userStorage.create(userMapper.toUser(userDto)));
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {
        return userMapper.toUserDto(userStorage.update(userMapper.toUser(userDto), userId));
    }

    @Override
    public UserDto getUserById(Long userId) {
        return userMapper.toUserDto(userStorage.getUserById(userId));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(Long userId) {
        userStorage.deleteUserById(userId);
    }
}
