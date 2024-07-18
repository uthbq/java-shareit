package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Long, User> users = new HashMap<>();
    private Long currentUserId = 0L;

    @Override
    public User create(User user) {
        checkUserEmail(user);
        user.setId(++currentUserId);
        users.put(user.getId(), user);
        log.info("Пользователь с id={}, успешно создан", user.getId());
        return user;
    }

    @Override
    public User update(User newUser, Long userId) {
        User user = getUserById(userId);
        if (newUser.getName() != null) {
            user.setName(newUser.getName());
        }
        if (newUser.getEmail() != null && !newUser.getEmail().equals(user.getEmail())) {
            checkUserEmail(newUser);
            user.setEmail(newUser.getEmail());
        }
        log.info("Информация о пользователе с id={}, успешно обновлена", userId);
        return user;
    }

    @Override
    public User getUserById(Long userid) {
        if (!users.containsKey(userid)) {
            throw new NotFoundException("Пользователь с таким id: " + userid + " не найден");
        }
        return users.get(userid);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Список всех пользователей получен успешно");
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUserById(Long userid) {
        getUserById(userid);
        log.info("Пользователь с id={},успешно удален", userid);
        users.remove(userid);
    }

    private void checkUserEmail(User user) {
        for (User savedUser : users.values()) {
            if (savedUser.getEmail().equals(user.getEmail())) {
                throw new ConflictException("Пользователь с таким email уже зарегистрирован");
            }
        }
    }

}
