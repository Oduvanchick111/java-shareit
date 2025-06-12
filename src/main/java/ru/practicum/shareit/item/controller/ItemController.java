package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestForUpdateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(("/items"))
public class ItemController {

    private final ItemService itemService;
    public static final String X_SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto addItem(@RequestHeader(X_SHARER_USER_ID_HEADER) Long userId, @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemService.saveItem(userId, itemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@RequestHeader(X_SHARER_USER_ID_HEADER) Long ownerId, @PathVariable Long itemId, @Valid @RequestBody ItemRequestForUpdateDto item) {
        return itemService.updateItem(ownerId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItem(@RequestHeader(X_SHARER_USER_ID_HEADER) Long ownerId, @PathVariable Long itemId) {
        return itemService.getItemById(ownerId, itemId);
    }

    @GetMapping
    public Collection<ItemResponseDto> getItemsByUserId(@RequestHeader(X_SHARER_USER_ID_HEADER) Long ownerId) {
        return itemService.getItemsByUserId(ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemResponseDto> getItemByText(@RequestHeader(X_SHARER_USER_ID_HEADER) Long ownerId, @RequestParam String text) {
        return itemService.getItemOnText(ownerId, text);
    }

    @DeleteMapping()
    public void deleteAllItems() {
        itemService.deleteAllItems();
    }
}
