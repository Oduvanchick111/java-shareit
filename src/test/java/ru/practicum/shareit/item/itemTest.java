package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class itemTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() throws Exception {
        mockMvc.perform(delete("/items"));
        mockMvc.perform(delete("/users"));
        String userJson = """
                {
                    "email": "pavelboltinskiy@gmail.com",
                    "name": "Pasha"
                }
                """;
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated());

        Long userId = 1L;
        String itemJson = """
                {
                    "name": "iphone",
                    "description": "16",
                    "available": true
                }
                """;
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetItem() throws Exception {
        mockMvc.perform(get("/items/1").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("iphone"));
    }

    @Test
    public void testCreateItemWithWrongParams() throws Exception {
        Long userId = 1L;
        String itemJson = """
                {
                    "name": "",
                    "description": "16",
                    "available": true
                }
                """;
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateItem() throws Exception {
        Long userId = 1L;
        String itemJson = """
                {
                    "description": "15"
                }
                """;
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isOk());

        mockMvc.perform(get("/items/1").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("15"));
    }

    @Test
    public void testGetItemByText() throws Exception {
        Long userId = 1L;
        String itemJson = """
                {
                    "name": "iphone16",
                    "description": "классный айфончик",
                    "available": true
                }
                """;
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isCreated());

        String text = "классный";

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("iphone16"));
    }
}
