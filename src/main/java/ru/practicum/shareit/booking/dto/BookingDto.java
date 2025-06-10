package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDate;

@Data
public class BookingDto {
    @NotNull
    private Long id;
    @NotNull
    @FutureOrPresent
    private LocalDate start;
    @NotNull
    @Future
    private LocalDate end;
    @NotNull
    private Long itemId;
    @NotNull
    private Long bookerId;
    @NotNull
    private Status status;

    @AssertTrue(message = "Дата окончания должна быть позже даты начала")
    private boolean isEndAfterStart() {
        return end.isAfter(start);
    }
}
