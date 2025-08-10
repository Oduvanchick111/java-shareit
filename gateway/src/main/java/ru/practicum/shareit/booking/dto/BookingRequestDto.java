package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public class BookingRequestDto {
        @NotNull
        @FutureOrPresent
        private LocalDateTime start;
        @NotNull
        @Future
        private LocalDateTime end;
        @NotNull
        private Long itemId;

        @AssertTrue(message = "Дата окончания должна быть позже даты начала")
        private boolean isEndAfterStart() {
            return end.isAfter(start);
        }
    }
