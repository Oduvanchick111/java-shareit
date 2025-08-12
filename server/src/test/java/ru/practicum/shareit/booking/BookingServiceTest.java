package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repo.BookingRepoJpa;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepoJpa;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepoJpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private UserRepoJpa userRepository;
    @Mock
    private ItemRepoJpa itemRepository;
    @Mock
    private BookingRepoJpa bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .name("Pavel")
                .email("pb@yandex.ru")
                .build();

        booker = User.builder()
                .id(2L)
                .name("Liza")
                .email("liza@yandex.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Iphone")
                .description("Description")
                .available(true)
                .owner(owner)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        bookingRequestDto = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    void create_shouldCreateBooking() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto result = bookingService.create(2L, bookingRequestDto);

        assertNotNull(result);
        assertEquals(Status.WAITING, result.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void create_byOwner_shouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidateException.class,
                () -> bookingService.create(1L, bookingRequestDto));
    }

    @Test
    void create_forUnavailableItem_shouldThrowException() {
        item.setAvailable(false);
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidateException.class,
                () -> bookingService.create(2L, bookingRequestDto));
    }

    @Test
    void update_approveByOwner_shouldUpdateStatus() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        BookingResponseDto result = bookingService.update(1L, 1L, true);

        assertEquals(Status.APPROVED, result.getStatus());
    }

    @Test
    void update_byNotOwner_shouldThrowException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidateException.class,
                () -> bookingService.update(3L, 1L, false));
    }

    @Test
    void getById_byOwner_shouldReturnBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        BookingResponseDto result = bookingService.getById(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getById_byOtherUser_shouldThrowException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidateException.class,
                () -> bookingService.getById(3L, 1L));
    }

    @Test
    void getAllByUser_shouldReturnBookings() {
        Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDesc(eq(2L), any(Pageable.class)))
                .thenReturn(page);

        List<BookingResponseDto> result = bookingService.getAllByUser(2L, State.ALL, 0, 10);

        assertEquals(1, result.size());
    }

    @Test
    void getAllByOwner_currentState_shouldReturnBookings() {
        Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findCurrentByOwnerId(eq(1L), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(page);

        List<BookingResponseDto> result = bookingService.getAllByOwner(1L, State.CURRENT, 0, 10);

        assertEquals(1, result.size());
    }
}
