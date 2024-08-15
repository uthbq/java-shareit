package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreatetDto;
import ru.practicum.shareit.booking.dto.BookingFullDTO;
import ru.practicum.shareit.booking.dto.BookingParams;

import java.util.List;

public interface BookingService {

    BookingFullDTO create(BookingCreatetDto booking, long bookerId);

    BookingFullDTO approved(BookingParams params);

    BookingFullDTO getById(BookingParams params);

    List<BookingFullDTO> getUserBookings(BookingState state, Long userId);

    List<BookingFullDTO> getOwnerBookings(BookingState state, Long userId);
}
