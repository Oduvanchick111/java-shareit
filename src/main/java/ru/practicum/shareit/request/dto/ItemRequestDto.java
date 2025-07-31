package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    private Long id;
    @NotBlank
    private String description;
    @NotBlank
    private User requestor;
    @Past
    private LocalDateTime created;
}