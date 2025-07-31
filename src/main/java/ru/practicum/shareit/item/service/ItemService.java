package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestForUpdateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.Collection;

public interface ItemService {
    ItemResponseDto saveItem(Long userId, ItemRequestDto itemRequestDto);

    ItemResponseDto updateItem(Long userId, Long itemId, ItemRequestForUpdateDto item);

    ItemResponseDto getById(Long userId, Long itemId);

    Collection<ItemResponseDto> getItemsByUserId(Long ownerId);

    Collection<ItemResponseDto> getItemOnText(Long userId, String text);

    void deleteAllItems();

    CommentDto addComment(Long userId, Long itemId, CommentDto comment);
}
