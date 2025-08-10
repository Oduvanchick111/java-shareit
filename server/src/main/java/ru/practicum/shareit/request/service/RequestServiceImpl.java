package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepoJpa;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.dto.ResponseDtoWithItems;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepoJpa;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final UserRepoJpa userRepoJpa;
    private final ItemRequestRepository requestRepository;
    private final ItemRepoJpa itemRepoJpa;

    @Transactional(readOnly = false)
    @Override
    public ResponseDto createRequest(Long userId, CreateItemRequestDto requestDto) {
        User user = userRepoJpa.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest request = new ItemRequest();
        request.setDescription(requestDto.getDescription());
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        request = requestRepository.save(request);
        return RequestMapper.toResponseDto(request);
    }

    @Override
    public List<ResponseDtoWithItems> getUserRequests(Long userId) {
        userRepoJpa.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return requestRepository
                .findByRequesterIdOrderByCreatedDesc(userId)
                .stream()
                .map(request -> {
                    List<Item> items = itemRepoJpa.findAllByRequestId(request.getId());
                    return RequestMapper.toResponseDtoWithItems(request, items);
                })
                .toList();
    }

    @Override
    public List<ResponseDto> getAllRequests() {
        return requestRepository.findAllByOrderByCreatedDesc().stream()
                .map(RequestMapper::toResponseDto)
                .toList();
    }

    @Override
    public ResponseDtoWithItems getRequest(Long userId, Long requestId) {
        return requestRepository.findById(requestId)
                .map(req -> {
                    List<Item> itemDtos = itemRepoJpa.findByOwnerIdOrderByIdAsc(userId).stream().toList();
                    return RequestMapper.toResponseDtoWithItems(req, itemDtos);
                })
                .orElseThrow(() -> new NotFoundException("Запрос с id = " + requestId + " для вещи не найден"));
    }
}
