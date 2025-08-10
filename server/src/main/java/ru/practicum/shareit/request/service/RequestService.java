package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.dto.ResponseDtoWithItems;

import java.util.List;

public interface RequestService {
    ResponseDto createRequest(Long userId, CreateItemRequestDto requestDto);

    List<ResponseDtoWithItems> getUserRequests(Long userId);

    List<ResponseDto> getAllRequests();

    ResponseDtoWithItems getRequest(Long requestId, Long userId);

}
