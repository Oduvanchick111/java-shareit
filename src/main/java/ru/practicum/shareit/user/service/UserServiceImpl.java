package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserRequestDtoForUpdate;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.UserDao;
import ru.practicum.shareit.user.repo.UserRepository;


import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public Collection<UserResponseDto> getAllUsers() {
        return repository.findAll().stream()
                .map(UserMapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto saveUser(UserRequestDto userDto) {
        if (!userDto.getEmail().isEmpty()) {
            if (repository.existsByEmail(userDto.getEmail())) {
                throw new EmailAlreadyExistsException("Такой email уже существует");
            }
        }
        UserDao dao = UserMapper.toUserDao(userDto);
        UserDao saved = repository.save(dao);
        return UserMapper.toUserResponseDto(saved);
    }

    @Override
    public UserResponseDto findUserById(Long userId) {
        return repository.findUserById(userId).map(UserMapper::toUserResponseDto).orElseThrow(() -> new NotFoundException("Такого пользователя не существует"));
    }

    @Override
    public UserResponseDto updateUser(Long userId, UserRequestDtoForUpdate userRequestDtoForUpdate) {
        if (repository.findUserById(userId).isEmpty()) {
            throw new NotFoundException("Такого пользователя не существует");
        }
        if (userRequestDtoForUpdate.getEmail() != null) {
            if (repository.existsByEmail(userRequestDtoForUpdate.getEmail())) {
                throw new EmailAlreadyExistsException("Такой email уже существует");
            }
        }
        UserDao userDao = UserMapper.toUserDaoUpdate(userRequestDtoForUpdate);
        userDao.setId(userId);
        repository.update(userId, userDao);
        return UserMapper.toUserResponseDto(userDao);
    }

    @Override
    public void deleteUser(Long userId) {
        if (repository.findUserById(userId).isEmpty()) {
            throw new NotFoundException("Такого пользователя не существует");
        } else {
            repository.delete(userId);
        }
    }

    @Override
    public void deleteAllUsers() {
        repository.deleteAll();
    }
}
