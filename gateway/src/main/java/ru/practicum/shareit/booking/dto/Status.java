package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
    WAITING("WAITING"), APPROVED("APPROVED"), REJECTED("REJECTED"), CANCELED("CANCELED");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
