package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ItemRequestDto {
    private Long id;
    @NotBlank
    private String description;
    @NotBlank
    private Long requestorId;
    @Past
    private LocalDate created;
}