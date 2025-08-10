package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.dto.ResponseDtoWithItems;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {

    public static final String X_SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto createRequest(@RequestHeader(value = X_SHARER_USER_ID_HEADER) Long userId, @RequestBody CreateItemRequestDto requestDto) {
        log.info("Received request from user {}: {}", userId, requestDto);
        return requestService.createRequest(userId, requestDto);
    }

    @GetMapping
    public List<ResponseDtoWithItems> getRequests(@RequestHeader(X_SHARER_USER_ID_HEADER) Long userId) {
        return requestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ResponseDto> getAllRequests() {
        return requestService.getAllRequests();
    }

    @GetMapping("/{requestId}")
    public ResponseDtoWithItems getRequestById(@RequestHeader(X_SHARER_USER_ID_HEADER) Long userId, @PathVariable Long requestId) {
        return requestService.getRequest(userId, requestId);
    }
}

