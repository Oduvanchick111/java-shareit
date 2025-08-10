package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestForUpdateDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;
    public static final String X_SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addItem(@RequestHeader(value = X_SHARER_USER_ID_HEADER) @NotNull @Positive Long userId, @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Create item {} by user = {}", itemRequestDto, userId);
        return itemClient.createItem(userId, itemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(X_SHARER_USER_ID_HEADER) Long ownerId, @PathVariable Long itemId, @Valid @RequestBody ItemRequestForUpdateDto item) {
        return itemClient.updateItem(ownerId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(X_SHARER_USER_ID_HEADER) Long ownerId, @PathVariable Long itemId) {
        return itemClient.getItem(ownerId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(@RequestHeader(X_SHARER_USER_ID_HEADER) Long ownerId) {
        return itemClient.getItemsByUserId(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemByText(@RequestHeader(X_SHARER_USER_ID_HEADER) Long ownerId, @RequestParam String text) {
        return itemClient.getItemByText(ownerId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(X_SHARER_USER_ID_HEADER) Long userId, @PathVariable Long itemId, @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
