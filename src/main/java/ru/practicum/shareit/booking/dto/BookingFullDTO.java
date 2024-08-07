package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingFullDTO {
    private Long id;
    private LocalDateTime start;

    private LocalDateTime end;

    private ItemDto item;
    private String itemName;

    private UserDto booker;
    private BookingStatus status;

    public record UserDto(Long id) {

    }

    public record ItemDto(Long id, String name) {

    }

}

