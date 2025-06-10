package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestForUpdateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.Collection;

public interface ItemService {
    ItemResponseDto saveItem(Long userId, ItemRequestDto itemRequestDto);

    ItemResponseDto updateItem(Long userId, Long itemId, ItemRequestForUpdateDto item);

    ItemResponseDto getItemById(Long userId, Long itemId);

    Collection<ItemResponseDto> getItemsByUserId(Long ownerId);

    Collection<ItemResponseDto> getItemOnText(Long userId, String text);

    void deleteAllItems();
}
