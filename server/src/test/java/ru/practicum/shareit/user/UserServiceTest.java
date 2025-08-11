package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserRequestDtoForUpdate;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepoJpa;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepoJpa repository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRequestDto userRequestDto;
    private UserRequestDtoForUpdate updateDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Pavel")
                .email("pb@yandex.ru")
                .build();

        userRequestDto = UserRequestDto.builder()
                .name("Pavel")
                .email("pb@yandex.ru")
                .build();

        updateDto = UserRequestDtoForUpdate.builder()
                .name("ne Pavel")
                .email("lzd@yandex.ru")
                .build();
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        when(repository.findAll()).thenReturn(List.of(user));

        Collection<UserResponseDto> result = userService.getAllUsers();

        assertEquals(1, result.size());
        verify(repository).findAll();
    }

    @Test
    void saveUser_withValidData_shouldSaveUser() {
        when(repository.save(any(User.class))).thenReturn(user);

        UserResponseDto result = userService.saveUser(userRequestDto);

        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        verify(repository).save(any(User.class));
    }

    @Test
    void findUserById_withExistingId_shouldReturnUser() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDto result = userService.findUserById(1L);

        assertEquals(user.getId(), result.getId());
        verify(repository).findById(1L);
    }

    @Test
    void findUserById_withNonExistingId_shouldThrowException() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findUserById(999L));
    }

    @Test
    void updateUser_updateName_shouldUpdateOnlyName() {
        UserRequestDtoForUpdate partialUpdate = UserRequestDtoForUpdate.builder()
                .name("New Name")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDto result = userService.updateUser(1L, partialUpdate);

        assertEquals("New Name", result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void updateUser_updateEmail_shouldUpdateOnlyEmail() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.existsByEmailAndIdNot(anyString(), anyLong())).thenReturn(false);

        UserResponseDto result = userService.updateUser(1L, updateDto);

        assertEquals(updateDto.getEmail(), result.getEmail());
        verify(repository).existsByEmailAndIdNot(updateDto.getEmail(), user.getId());
    }

    @Test
    void updateUser_withExistingEmail_shouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.existsByEmailAndIdNot(updateDto.getEmail(), user.getId())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.updateUser(1L, updateDto));
    }

    @Test
    void deleteUser_withExistingId_shouldDeleteUser() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(repository).delete(user);
    }

    @Test
    void deleteAllUsers_shouldCallDeleteAll() {
        userService.deleteAllUsers();

        verify(repository).deleteAll();
    }
}
