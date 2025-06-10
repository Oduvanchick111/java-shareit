package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.repo.UserRepositoryImpl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserTest {

    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    public void setUp() throws Exception {
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
    }

    @Test
    public void testGetUser() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pasha"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void testUpdateUser() throws Exception {
        String userJson = """
                {
                    "name": "NePasha"
                }
                """;
        mockMvc.perform(patch("/users/1").contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NePasha"));
    }

    @Test
    public void testCreateUserWithWrongEmail() throws Exception {
        String userJson = """
                {
                    "email": "padsadsa",
                    "name": "Pasha"
                }
                """;
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateUserWithSameEmail() throws Exception {
        String userJson = """
                {
                    "email": "pavelboltinskiy@gmail.com",
                    "name": "NePasha"
                }
                """;
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isConflict());
    }

    @Test
    public void testFindUserWithWrongId() throws Exception {
        mockMvc.perform(get("/users/2"))
                .andExpect(status().isNotFound());
    }
}
