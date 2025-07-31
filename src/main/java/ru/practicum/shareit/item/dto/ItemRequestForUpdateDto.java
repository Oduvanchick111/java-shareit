package ru.practicum.shareit.item.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ItemRequestForUpdateDto {
    @Nullable
    private Long id;
    @Nullable
    @Size(max = 200)
    private String name;
    @Nullable
    @Size(max = 200)
    private String description;
    @Nullable
    private Boolean available;
    @Nullable
    private Long ownerId;
    private Long requestId;
}
