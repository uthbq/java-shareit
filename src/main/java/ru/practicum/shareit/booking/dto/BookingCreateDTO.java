package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.validator.StartBeforeEnd;
import ru.practicum.shareit.validator.StartEnd;

import java.time.LocalDateTime;

@Data
@StartBeforeEnd
public class BookingCreateDTO implements StartEnd {

    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    @Future
    private LocalDateTime end;
    @NotNull
    private Long itemId;

    private User booker;

    @AssertFalse
    public boolean isDateValid() {
        if (start != null && end != null) {
            return (end.isBefore(start) || end.isBefore(LocalDateTime.now()) ||
                    start.isBefore(LocalDateTime.now()) || end.isEqual(start));
        } else {
            return true;
        }
    }

}
