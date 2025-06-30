package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDto;

@UtilityClass
public class BookingMapper {
    public BookingDao toBookingDao(BookingDto bookingDto) {
        return BookingDao.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .bookerId(bookingDto.getBookerId())
                .itemId(bookingDto.getItemId())
                .status(bookingDto.getStatus())
                .build();
    }
}
