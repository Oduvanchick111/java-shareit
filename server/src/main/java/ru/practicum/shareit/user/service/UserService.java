package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserRequestDtoForUpdate;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.Collection;

public interface UserService {
    Collection<UserResponseDto> getAllUsers();

    UserResponseDto saveUser(UserRequestDto userDto);

    UserResponseDto findUserById(Long userId);

    UserResponseDto updateUser(Long userId, UserRequestDtoForUpdate userRequestDtoForUpdate);

    void deleteUser(Long userId);

    void deleteAllUsers();
}
