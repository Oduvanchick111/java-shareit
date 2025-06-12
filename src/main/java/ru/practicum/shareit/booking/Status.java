package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
    WAITING("Новое бронирование, ожидает одобрения"), APPROVED("Бронирование подтверждено владельцем"), REJECTED("Бронирование отклонено владельцем"), CANCELED("Бронирование отменено создателем");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
