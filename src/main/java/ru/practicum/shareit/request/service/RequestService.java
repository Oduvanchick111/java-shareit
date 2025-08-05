package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.dto.ResponseDtoWithItems;

import java.util.List;

public interface RequestService {
    ResponseDto createRequest(RequestDto requestDto);

    List<ResponseDtoWithItems> getRequests(Long userId);

    List<ResponseDto> getAllRequests(Long userId, Integer from, Integer size);

    ResponseDtoWithItems getRequestById(Long requestId);

}
