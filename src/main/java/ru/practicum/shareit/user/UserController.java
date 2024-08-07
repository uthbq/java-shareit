package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.marker.Marker;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;


@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        log.info("GET ==> /users");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    @Validated
    public UserDto getById(@PathVariable @Min(0) long id) {
        log.info("GET ==> /users");
        UserDto saveUser = userService.getById(id);
        log.info("<== GET /users {}", saveUser);
        return saveUser;
    }


    @PatchMapping("/{userId}")
    @Validated({Marker.Update.class})
    public UserDto update(@Valid @RequestBody UserDto user,
                          @PathVariable(value = "userId") long userId) {
        log.info("PATCH ==> /users/{}", userId);
        user.setId(userId);
        UserDto updateUser = userService.update(user);
        log.info("<== PATCH /users/{}", userId);
        return updateUser;
    }

    @PostMapping
    @Validated({Marker.Create.class})
    public UserDto create(@Valid @RequestBody UserDto user) {
        log.info("==>POST /users {}", user);
        UserDto createdUser = userService.create(user);
        log.info("POST /users <== {}", user);
        return createdUser;
    }

    @DeleteMapping("/{id}")
    @Validated
    public void delete(@PathVariable("id") @Min(0) long id) {
        log.info("==>DELETE /users {}", id);
        userService.deleteUserById(id);
    }

}
