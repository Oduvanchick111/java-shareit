package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserRequestDtoForUpdate;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto saveNewUser(@Valid @RequestBody UserRequestDto user) {
        return userService.saveUser(user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @DeleteMapping()
    public void deleteAllUsers() {
        userService.deleteAllUsers();
    }

    @PatchMapping("/{userId}")
    public UserResponseDto updateUser(@PathVariable Long userId, @Valid @RequestBody UserRequestDtoForUpdate userRequestDtoForUpdate) {
        return userService.updateUser(userId, userRequestDtoForUpdate);
    }

    @GetMapping("/{userId}")
    public UserResponseDto findUserById(@PathVariable Long userId) {
        return userService.findUserById(userId);
    }
}
