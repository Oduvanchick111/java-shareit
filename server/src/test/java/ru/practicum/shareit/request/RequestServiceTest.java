package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepoJpa;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.dto.ResponseDtoWithItems;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.ItemRequestRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepoJpa;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {
    @Mock
    private UserRepoJpa userRepoJpa;
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private ItemRepoJpa itemRepoJpa;

    @InjectMocks
    private RequestServiceImpl requestService;

    private User user;
    private ItemRequest request;
    private CreateItemRequestDto createDto;
    private Item item;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .build();

        request = ItemRequest.builder()
                .id(1L)
                .description("Need item")
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        createDto = CreateItemRequestDto.builder()
                .description("Need item")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .owner(user)
                .build();
    }

    @Test
    void createRequest_shouldCreateRequest() {
        when(userRepoJpa.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ResponseDto result = requestService.createRequest(1L, createDto);

        assertNotNull(result);
        assertEquals(request.getDescription(), result.getDescription());
        verify(requestRepository).save(any(ItemRequest.class));
    }

    @Test
    void createRequest_withNonExistingUser_shouldThrowException() {
        when(userRepoJpa.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> requestService.createRequest(999L, createDto));
    }

    @Test
    void getUserRequests_shouldReturnRequestsWithItems() {
        when(userRepoJpa.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findByRequesterIdOrderByCreatedDesc(1L))
                .thenReturn(List.of(request));
        when(itemRepoJpa.findAllByRequestId(1L)).thenReturn(List.of(item));

        List<ResponseDtoWithItems> result = requestService.getUserRequests(1L);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getItems().size());
        verify(requestRepository).findByRequesterIdOrderByCreatedDesc(1L);
    }

    @Test
    void getAllRequests_shouldReturnAllRequests() {
        when(requestRepository.findAllByOrderByCreatedDesc())
                .thenReturn(List.of(request));

        List<ResponseDto> result = requestService.getAllRequests();

        assertEquals(1, result.size());
        verify(requestRepository).findAllByOrderByCreatedDesc();
    }

    @Test
    void getRequest_shouldReturnRequestWithItems() {
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepoJpa.findByOwnerIdOrderByIdAsc(1L)).thenReturn(List.of(item));

        ResponseDtoWithItems result = requestService.getRequest(1L, 1L);

        assertNotNull(result);
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void getRequest_withNonExistingId_shouldThrowException() {
        when(requestRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> requestService.getRequest(1L, 999L));
    }

    @Test
    void getRequest_withNoItems_shouldReturnEmptyItemsList() {
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepoJpa.findByOwnerIdOrderByIdAsc(1L)).thenReturn(Collections.emptyList());

        ResponseDtoWithItems result = requestService.getRequest(1L, 1L);

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
    }
}
