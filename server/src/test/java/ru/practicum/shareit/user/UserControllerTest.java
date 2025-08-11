package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserRequestDtoForUpdate;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserResponseDto userResponseDto;
    private UserRequestDto userRequestDto;
    private UserRequestDtoForUpdate updateDto;

    @BeforeEach
    void setUp() {
        userResponseDto = UserResponseDto.builder()
                .id(1L)
                .name("Pavel")
                .email("pb@yandex.ru")
                .build();

        userRequestDto = UserRequestDto.builder()
                .name("Pavel")
                .email("pb@yandex.ru")
                .build();

        updateDto = UserRequestDtoForUpdate.builder()
                .name("Updated Name")
                .email("updated@yandex.ru")
                .build();
    }

    @Test
    void getAllUsers_shouldReturnUserList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(userResponseDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Pavel"));

        verify(userService).getAllUsers();
    }

    @Test
    void saveNewUser_shouldCreateUser() throws Exception {
        when(userService.saveUser(any(UserRequestDto.class))).thenReturn(userResponseDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

        verify(userService).saveUser(any(UserRequestDto.class));
    }

    @Test
    void saveNewUser_withInvalidData_shouldReturnBadRequest() throws Exception {
        UserRequestDto invalidDto = new UserRequestDto(); // Пустой объект

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).saveUser(any());
    }

    @Test
    void deleteUser_shouldCallService() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService).deleteUser(1L);
    }

    @Test
    void deleteAllUsers_shouldCallService() throws Exception {
        doNothing().when(userService).deleteAllUsers();

        mockMvc.perform(delete("/users"))
                .andExpect(status().isOk());

        verify(userService).deleteAllUsers();
    }

    @Test
    void updateUser_shouldUpdateUser() throws Exception {
        when(userService.updateUser(anyLong(), any(UserRequestDtoForUpdate.class))).thenReturn(userResponseDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(userService).updateUser(eq(1L), any(UserRequestDtoForUpdate.class));
    }

    @Test
    void findUserById_shouldReturnUser() throws Exception {
        when(userService.findUserById(anyLong())).thenReturn(userResponseDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(userService).findUserById(1L);
    }

    @Test
    void findUserById_withNonExistingId_shouldReturnNotFound() throws Exception {
        when(userService.findUserById(anyLong())).thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());

        verify(userService).findUserById(999L);
    }
}
