package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserResponseDto {
    private Long id;
    private String email;
    private String name;
}
