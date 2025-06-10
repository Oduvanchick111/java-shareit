package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestForUpdateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.ItemDao;

public class ItemMapper {

    public static ItemDao toItemDao(ItemRequestDto itemRequestDto) {
        return ItemDao.builder()
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .available(itemRequestDto.getAvailable())
                .ownerId(itemRequestDto.getOwnerId())
                .requestId(itemRequestDto.getRequestId())
                .build();
    }

    public static ItemDao toItemDaoUpdate(ItemRequestForUpdateDto itemRequestDto) {
        return ItemDao.builder()
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .available(itemRequestDto.getAvailable())
                .ownerId(itemRequestDto.getOwnerId())
                .requestId(itemRequestDto.getRequestId())
                .build();
    }

    public static ItemResponseDto toItemResponseDto(ItemDao itemDao) {
        return ItemResponseDto.builder()
                .id(itemDao.getId())
                .name(itemDao.getName())
                .description(itemDao.getDescription())
                .available(itemDao.getAvailable())
                .requestId(itemDao.getRequestId())
                .ownerId(itemDao.getOwnerId())
                .build();
    }
}
