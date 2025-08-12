package ru.practicum.shareit.user.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDtoForUpdate {
    @Email
    @Nullable
    private String email;
    @Nullable
    @Size(max = 200)
    private String name;
}
