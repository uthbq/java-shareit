package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Item;

@Component
public class BookingMapper {

    public BookingFullDTO mapToDTO(Booking model) {
        return BookingFullDTO.builder()
                .id(model.getId())
                .start(model.getStart())
                .end(model.getEnd())
                .booker(new BookingFullDTO.UserDto(model.getBooker().getId()))
                .status(model.getStatus())
                .itemName(model.getItem().getName())
                .item(new BookingFullDTO.ItemDto(model.getItem().getId(), model.getItem().getName()))
                .build();
    }

    public Booking mapToBooking(BookingCreateDTO dto, Item item) {
        return Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .booker(dto.getBooker())
                .item(item)
                .build();
    }

    public Booking mapToBookingInfo(Booking booking) {
        return Booking.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .build();
    }
}
