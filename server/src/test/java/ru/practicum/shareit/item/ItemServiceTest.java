package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repo.BookingRepoJpa;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestForUpdateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.item.repo.ItemRepoJpa;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepoJpa;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepoJpa itemRepo;
    @Mock
    private UserRepoJpa userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepoJpa bookingRepoJpa;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private Item item;
    private ItemRequestDto itemRequestDto;
    private ItemRequestForUpdateDto updateDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .name("Pavel")
                .email("pb@yandex.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Iphone")
                .description("Description")
                .available(true)
                .owner(owner)
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .name("Android")
                .description("Description")
                .available(true)
                .build();

        updateDto = ItemRequestForUpdateDto.builder()
                .name("NOKIA")
                .description("Updated Description")
                .available(false)
                .build();

        commentDto = CommentDto.builder()
                .text("Test comment")
                .build();
    }

    @Test
    void saveItem_shouldSaveItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepo.save(any(Item.class))).thenReturn(item);

        ItemResponseDto result = itemService.saveItem(1L, itemRequestDto);

        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
        verify(itemRepo).save(any(Item.class));
    }

    @Test
    void saveItem_withRequest_shouldSaveItemWithRequest() {
        ItemRequest request = new ItemRequest();
        itemRequestDto.setRequestId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepo.save(any(Item.class))).thenReturn(item);

        ItemResponseDto result = itemService.saveItem(1L, itemRequestDto);

        assertNotNull(result);
        verify(itemRequestRepository).findById(1L);
    }

    @Test
    void saveItem_withNonExistingUser_shouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.saveItem(999L, itemRequestDto));
    }

    @Test
    void updateItem_shouldUpdateFields() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));

        ItemResponseDto result = itemService.updateItem(1L, 1L, updateDto);

        assertEquals(updateDto.getName(), result.getName());
        assertEquals(updateDto.getDescription(), result.getDescription());
        assertEquals(updateDto.getAvailable(), result.getAvailable());
    }

    @Test
    void updateItem_withNotOwner_shouldThrowException() {
        User otherUser = User.builder().id(2L).build();
        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser));
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidateException.class, () -> itemService.updateItem(2L, 1L, updateDto));
    }

    @Test
    void getById_shouldReturnItemWithDetails() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(1L)).thenReturn(Collections.emptyList());

        ItemResponseDto result = itemService.getById(1L, 1L);

        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
        assertTrue(result.getComments().isEmpty());
    }

    @Test
    void getById_withBookings_shouldIncludeBookingInfo() {
        User booker = User.builder().id(2L).name("Booker").build();
        Booking lastBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .booker(booker)
                .status(Status.APPROVED)
                .build();
        Booking nextBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(1L)).thenReturn(Collections.emptyList());
        when(bookingRepoJpa.findTopByItemIdAndStartBeforeAndStatusOrderByEndDesc(
                eq(1L), any(LocalDateTime.class), eq(Status.APPROVED)))
                .thenReturn(lastBooking);
        when(bookingRepoJpa.findTopByItemIdAndStartAfterAndStatusOrderByStartAsc(
                eq(1L), any(LocalDateTime.class), eq(Status.APPROVED)))
                .thenReturn(nextBooking);

        ItemResponseDto result = itemService.getById(1L, 1L);

        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
        assertEquals(1L, result.getLastBooking().getId());
        assertEquals(2L, result.getNextBooking().getId());
    }

    @Test
    void getItemsByUserId_shouldReturnUserItems() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepo.findByOwnerIdOrderByIdAsc(1L)).thenReturn(Collections.singletonList(item));

        Collection<ItemResponseDto> result = itemService.getItemsByUserId(1L);

        assertEquals(1, result.size());
        verify(itemRepo).findByOwnerIdOrderByIdAsc(1L);
    }

    @Test
    void getItemOnText_shouldReturnMatchingItems() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepo.getItemOnText("test")).thenReturn(Collections.singletonList(item));

        Collection<ItemResponseDto> result = itemService.getItemOnText(1L, "test");

        assertEquals(1, result.size());
    }

    @Test
    void getItemOnText_withBlankText_shouldReturnEmptyList() {
        Collection<ItemResponseDto> result = itemService.getItemOnText(1L, " ");

        assertTrue(result.isEmpty());
    }

    @Test
    void addComment_shouldAddComment() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepoJpa.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                eq(1L), eq(1L), eq(Status.APPROVED), any(LocalDateTime.class)))
                .thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        CommentDto result = itemService.addComment(1L, 1L, commentDto);

        assertNotNull(result);
        assertEquals(commentDto.getText(), result.getText());
    }

    @Test
    void addComment_withoutBooking_shouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepoJpa.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                anyLong(), anyLong(), any(), any()))
                .thenReturn(false);

        assertThrows(ValidateException.class, () -> itemService.addComment(1L, 1L, commentDto));
    }

    @Test
    void deleteAllItems_shouldCallDeleteAll() {
        itemService.deleteAllItems();
        verify(itemRepo).deleteAll();
    }
}
