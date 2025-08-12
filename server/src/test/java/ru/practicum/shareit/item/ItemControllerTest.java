package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestForUpdateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemResponseDto itemResponseDto;
    private ItemRequestDto itemRequestDto;
    private ItemRequestForUpdateDto updateDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Iphone")
                .description("Description")
                .available(true)
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .name("Iphone")
                .description("Description")
                .available(true)
                .build();

        updateDto = ItemRequestForUpdateDto.builder()
                .name("Android")
                .description("Updated Description")
                .available(false)
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("Kirpich!")
                .authorName("User")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void addItem_shouldCreateItem() throws Exception {
        when(itemService.saveItem(anyLong(), any(ItemRequestDto.class))).thenReturn(itemResponseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Iphone"));

        verify(itemService).saveItem(eq(1L), any(ItemRequestDto.class));
    }

    @Test
    void updateItem_shouldUpdateItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemRequestForUpdateDto.class)))
                .thenReturn(itemResponseDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(itemService).updateItem(eq(1L), eq(1L), any(ItemRequestForUpdateDto.class));
    }

    @Test
    void getItem_shouldReturnItem() throws Exception {
        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemResponseDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(itemService).getById(eq(1L), eq(1L));
    }

    @Test
    void getItemsByUserId_shouldReturnItems() throws Exception {
        when(itemService.getItemsByUserId(anyLong())).thenReturn(List.of(itemResponseDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(itemService).getItemsByUserId(eq(1L));
    }

    @Test
    void getItemByText_shouldReturnItems() throws Exception {
        when(itemService.getItemOnText(anyLong(), anyString())).thenReturn(List.of(itemResponseDto));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(itemService).getItemOnText(eq(1L), eq("test"));
    }

    @Test
    void getItemByText_withEmptyText_shouldReturnEmptyList() throws Exception {
        when(itemService.getItemOnText(anyLong(), anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(itemService).getItemOnText(eq(1L), eq(""));
    }

    @Test
    void deleteAllItems_shouldCallService() throws Exception {
        doNothing().when(itemService).deleteAllItems();

        mockMvc.perform(delete("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(itemService).deleteAllItems();
    }

    @Test
    void addComment_shouldCreateComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Kirpich!"));

        verify(itemService).addComment(eq(1L), eq(1L), any(CommentDto.class));
    }
}
