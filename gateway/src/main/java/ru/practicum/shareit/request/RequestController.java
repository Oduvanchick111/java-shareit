package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.Map;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;
    public static final String X_SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(
            @RequestHeader(value = X_SHARER_USER_ID_HEADER) @NotNull @Positive Long userId,
            @Valid @RequestBody RequestDto requestDto
    ) {
        log.info("Create item request {} by userId = {}", requestDto, userId);
        return requestClient.createRequest(userId, requestDto);
    }

    @GetMapping()
    public ResponseEntity<Object> getRequests(@RequestHeader(X_SHARER_USER_ID_HEADER) Long userId) {
        return requestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests() {
        return requestClient.getAllRequests();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(X_SHARER_USER_ID_HEADER) Long userId, @PathVariable long requestId) {
        log.info("Запрос на получения реквеста с id {}", requestId);
        return requestClient.getRequestById(userId, requestId);
    }
}
