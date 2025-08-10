package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.dto.ResponseDtoWithItems;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@UtilityClass
public class RequestMapper {

    public ItemRequest toDao(RequestDto requestDto, User requester) {
        return ItemRequest.builder()
                .description(requestDto.getDescription())
                .requester(requester)
                .build();
    }

    public RequestDto toRequestDto(ItemRequest itemRequest) {
        return RequestDto.builder()
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requesterId(itemRequest.getRequester() != null ? itemRequest.getRequester().getId() : null)
                .build();
    }

    public ResponseDto toResponseDto(ItemRequest itemRequest) {
        return ResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requesterId(itemRequest.getRequester() != null ? itemRequest.getRequester().getId() : null)
                .created(itemRequest.getCreated())
                .build();
    }

    public ResponseDtoWithItems toResponseDtoWithItems(ItemRequest itemRequest, List<Item> items) {
        List <ItemResponseDto> itemsResponse = items.stream().map(ItemMapper::toItemResponseDto).toList();
        return ResponseDtoWithItems.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requesterId(itemRequest.getRequester() != null ? itemRequest.getRequester().getId() : null)
                .created(itemRequest.getCreated())
                .items(itemsResponse)
                .build();
    }
}
