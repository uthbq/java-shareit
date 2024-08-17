package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

