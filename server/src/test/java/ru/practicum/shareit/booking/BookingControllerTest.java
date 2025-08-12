package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingResponseDto bookingResponseDto;
    private BookingRequestDto bookingRequestDto;
    private UserResponseDto userResponseDto;
    private ItemResponseDto itemResponseDto;

    @BeforeEach
    void setUp() {
        userResponseDto = UserResponseDto.builder()
                .id(1L)
                .name("Booker")
                .email("booker@example.com")
                .build();

        itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        bookingResponseDto = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.WAITING)
                .booker(userResponseDto)
                .item(itemResponseDto)
                .build();

        bookingRequestDto = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    void addBooking_shouldCreateBooking() throws Exception {
        when(bookingService.create(anyLong(), any(BookingRequestDto.class)))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.booker.id").value(1L))
                .andExpect(jsonPath("$.item.id").value(1L));

        verify(bookingService).create(eq(1L), any(BookingRequestDto.class));
    }

    @Test
    void update_approveBooking_shouldUpdateStatus() throws Exception {
        when(bookingService.update(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(bookingService).update(eq(1L), eq(1L), eq(true));
    }

    @Test
    void getBooking_shouldReturnBooking() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.id").value(1L));

        verify(bookingService).getById(eq(1L), eq(1L));
    }

    @Test
    void getAllByUser_shouldReturnBookings() throws Exception {
        when(bookingService.getAllByUser(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings?state=ALL")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(bookingService).getAllByUser(eq(1L), eq(State.ALL), eq(0), eq(10));
    }

    @Test
    void getAllByOwner_shouldReturnOwnerBookings() throws Exception {
        when(bookingService.getAllByOwner(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings/owner?state=FUTURE")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(bookingService).getAllByOwner(eq(1L), eq(State.FUTURE), eq(0), eq(10));
    }

    @Test
    void getAllByUser_withPagination_shouldUsePageable() throws Exception {
        when(bookingService.getAllByUser(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings?state=ALL&from=10&size=5")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(bookingService).getAllByUser(eq(1L), eq(State.ALL), eq(10), eq(5));
    }

    @Test
    void getAllByUser_withInvalidState_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings?state=312312")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }
}
