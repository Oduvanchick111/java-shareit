package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserRequestDtoForUpdate;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;


    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> saveNewUser(@Valid @RequestBody UserRequestDto user) {
        return userClient.createUser(user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable @NotNull @Positive Long userId) {
        log.info("Delete user {}", userId);
        return userClient.deleteUserById(userId);
    }

    @DeleteMapping()
    public ResponseEntity<Object> deleteAllUsers() {
        log.info("Delete all users");
        return userClient.deleteAllUsers();
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @Valid @RequestBody UserRequestDtoForUpdate userRequestDtoForUpdate) {
        return userClient.updateUser(userId, userRequestDtoForUpdate);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findUserById(@PathVariable @NotNull @Positive Long userId) {
        log.info("Get user {}", userId);
        return userClient.getUserById(userId);
    }
}
