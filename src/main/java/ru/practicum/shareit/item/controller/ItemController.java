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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemService.saveItem(userId, itemRequestDto);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponseDto updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable Long itemId, @Valid @RequestBody ItemRequestForUpdateDto item) {
        return itemService.updateItem(ownerId, itemId, item);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponseDto getItem(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable Long itemId) {
        return itemService.getItemById(ownerId, itemId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemResponseDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getItemsByUserId(ownerId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemResponseDto> getItemByText(@RequestHeader("X-Sharer-User-Id") Long ownerId, @RequestParam String text) {
        return itemService.getItemOnText(ownerId, text);
    }
}
