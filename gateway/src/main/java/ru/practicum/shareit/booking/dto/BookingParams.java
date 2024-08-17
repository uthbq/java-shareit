package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingParams {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long userId;
    private Boolean approved;
}
