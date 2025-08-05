package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.dto.ResponseDtoWithItems;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@UtilityClass
public class RequestMapper {

    public ItemRequest toDao(RequestDto requestDto) {
        return ItemRequest.builder()
                .description(requestDto.getDescription())
                .requestor(requestDto.getRequestor())
                .created(requestDto.getCreated())
                .build();
    }

    public RequestDto toRequestDto(ItemRequest itemRequest) {
        return RequestDto.builder()
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())
                .created(itemRequest.getCreated())
                .build();
    }

    public ResponseDto toResponseDto(ItemRequest itemRequest) {
        return ResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())
                .created(itemRequest.getCreated())
                .build();
    }

    public ResponseDtoWithItems toResponseDtoWithItems(ItemRequest itemRequest, List<Item> items) {
        List <ItemResponseDto> itemsResponse = items.stream().map(ItemMapper::toItemResponseDto).toList();
        return ResponseDtoWithItems.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())
                .created(itemRequest.getCreated())
                .items(itemsResponse)
                .build();
    }
}
