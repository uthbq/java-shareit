package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;


@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        log.info("GET ==> /users");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        log.info("GET ==> /users");
        UserDto saveUser = userService.getById(id);
        log.info("<== GET /users {}", saveUser);
        return saveUser;
    }


    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto user,
                          @PathVariable(value = "userId") long userId) {
        log.info("PATCH ==> /users/{}", userId);
        user.setId(userId);
        UserDto updateUser = userService.update(user);
        log.info("<== PATCH /users/{}", userId);
        return updateUser;
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto user) {
        log.info("==>POST /users {}", user);
        UserDto createdUser = userService.create(user);
        log.info("POST /users <== {}", user);
        return createdUser;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") long id) {
        log.info("==>DELETE /users {}", id);
        userService.deleteUserById(id);
    }

}
