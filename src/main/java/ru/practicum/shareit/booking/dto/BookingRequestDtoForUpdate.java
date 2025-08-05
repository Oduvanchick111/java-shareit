package ru.practicum.shareit.booking.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Builder
@Data
public class BookingRequestDtoForUpdate {
    @Nullable
    private Long id;
    @Nullable
    @FutureOrPresent
    private LocalDateTime start;
    @Nullable
    @Future
    private LocalDateTime end;
    @Nullable
    private Long itemId;
    @Nullable
    private Status status;

    @AssertTrue(message = "Дата окончания должна быть позже даты начала")
    private boolean isEndAfterStart() {
        return end.isAfter(start);
    }
}
