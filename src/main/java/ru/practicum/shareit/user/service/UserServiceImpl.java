package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserRequestDtoForUpdate;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repoJPA.UserRepoJpa;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepoJpa repository;

    @Override
    public Collection<UserResponseDto> getAllUsers() {
        return repository.findAll().stream()
                .map(UserMapper::toUserResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public UserResponseDto saveUser(UserRequestDto userDto) {
        User user = UserMapper.toUserDao(userDto);
        User savedUser = repository.save(user);
        return UserMapper.toUserResponseDto(savedUser);
    }

    @Override
    public UserResponseDto findUserById(Long userId) {
        return repository.findById(userId).map(UserMapper::toUserResponseDto).orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));
    }

    @Override
    public UserResponseDto updateUser(Long userId, UserRequestDtoForUpdate userRequestDtoForUpdate) {
        User existingUser = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Юзер с id: %d не найден", userId)));
        if (userRequestDtoForUpdate.getName() != null) {
            existingUser.setName(userRequestDtoForUpdate.getName());
        }
        if (userRequestDtoForUpdate.getEmail() != null) {
            existingUser.setEmail(userRequestDtoForUpdate.getEmail());
        }
        User updatedUser = repository.save(existingUser);
        return UserMapper.toUserResponseDto(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        User existingUser = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Юзер с id: %d не найден", userId)));
        repository.delete(existingUser);
    }

    @Override
    public void deleteAllUsers() {
        repository.deleteAll();
    }
}
