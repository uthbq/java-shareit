package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;


@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("GET ==> /users");
        return userClient.getUsers();
    }

    @GetMapping("/{id}")
    @Validated
    public ResponseEntity<Object> getById(@PathVariable @Min(0) long id) {
        log.info("GET ==> /users");
        ResponseEntity<Object> saveUser = userClient.getUser(id);
        log.info("<== GET /users {}", saveUser);
        return saveUser;
    }


    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@Valid @RequestBody UserUpdateDto user,
                                         @PathVariable(value = "userId") long userId) {
        log.info("PATCH ==> /users/{}", userId);
        user.setId(userId);
        ResponseEntity<Object> updateUser = userClient.updateUser(user, userId);
        log.info("<== PATCH /users/{}", userId);
        return updateUser;
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserCreateDto user) {
        log.info("==>POST /users {}", user);
        ResponseEntity<Object> createdUser = userClient.createUser(user);
        log.info("POST /users <== {}", user);
        return createdUser;
    }

    @DeleteMapping("/{id}")
    @Validated
    public void delete(@PathVariable("id") @Min(0) long id) {
        log.info("==>DELETE /users {}", id);
        userClient.deleteUser(id);
    }

}
