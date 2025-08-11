package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.dto.ResponseDtoWithItems;
import ru.practicum.shareit.request.service.RequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
@AutoConfigureMockMvc
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    @Autowired
    private ObjectMapper objectMapper;

    private ResponseDto responseDto;
    private ResponseDtoWithItems responseDtoWithItems;
    private CreateItemRequestDto createItemRequestDto;

    @BeforeEach
    void setUp() {
        responseDto = ResponseDto.builder()
                .id(1L)
                .description("Need item")
                .created(LocalDateTime.now())
                .build();

        responseDtoWithItems = ResponseDtoWithItems.builder()
                .id(1L)
                .description("Need item with response")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        createItemRequestDto = CreateItemRequestDto.builder()
                .description("New request")
                .build();
    }

    @Test
    void createRequest_shouldCreateRequest() throws Exception {
        when(requestService.createRequest(anyLong(), any(CreateItemRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createItemRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").exists());

        verify(requestService).createRequest(eq(1L), any(CreateItemRequestDto.class));
    }


    @Test
    void getRequests_shouldReturnUserRequests() throws Exception {
        when(requestService.getUserRequests(anyLong()))
                .thenReturn(List.of(responseDtoWithItems));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].items").isArray());

        verify(requestService).getUserRequests(eq(1L));
    }

    @Test
    void createRequest_withInvalidData_shouldReturnBadRequest() throws Exception {
        CreateItemRequestDto invalidDto = new CreateItemRequestDto();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).createRequest(anyLong(), any());
    }

    @Test
    void getAllRequests_shouldReturnAllRequests() throws Exception {
        when(requestService.getAllRequests())
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").exists());

        verify(requestService).getAllRequests();
    }

    @Test
    void getRequestById_shouldReturnRequest() throws Exception {
        when(requestService.getRequest(anyLong(), anyLong()))
                .thenReturn(responseDtoWithItems);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.items").isArray());

        verify(requestService).getRequest(eq(1L), eq(1L));
    }

    @Test
    void getRequestById_whenNotFound_shouldReturnNotFound() throws Exception {
        when(requestService.getRequest(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Request not found"));

        mockMvc.perform(get("/requests/999")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());

        verify(requestService).getRequest(eq(1L), eq(999L));
    }
}
