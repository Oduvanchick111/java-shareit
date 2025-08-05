package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepoJpa;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.dto.ResponseDtoWithItems;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.ItemRequestRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepoJpa itemRepoJpa;

    @Override
    public ResponseDto createRequest(RequestDto requestDto) {
        ItemRequest itemRequest = RequestMapper.toDao(requestDto);
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        return RequestMapper.toResponseDto(savedRequest);
    }

    @Override
    public List<ResponseDtoWithItems> getRequests(Long userId) {
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId);
        return itemRequests.stream()
                .map(request -> {
                    List<Item> items = itemRepoJpa.findAllByRequestId(request.getId());
                    return RequestMapper.toResponseDtoWithItems(request, items);
                })
                .toList();
    }

    @Override
    public List<ResponseDto> getAllRequests(Long userId, Integer from, Integer size) {
        Pageable page = PageRequest.of(
                from / size,
                size,
                Sort.by("created").descending()
        );
        Page<ItemRequest> requestsPage = itemRequestRepository
                .findAllByRequestorIdNot(userId, page);

        return requestsPage.getContent().stream()
                .map(RequestMapper::toResponseDto)
                .toList();
    }

    @Override
    public ResponseDtoWithItems getRequestById(Long requestId) {
        ItemRequest existingRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(String.format("Request с id: %d не найден", requestId)));
        List<Item> items = itemRepoJpa.findAllByRequestId(requestId);
        return RequestMapper.toResponseDtoWithItems(existingRequest, items);
    }
}
