package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreatetDto;
import ru.practicum.shareit.booking.dto.BookingFullDTO;
import ru.practicum.shareit.booking.dto.BookingParams;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingFullDTO create(@RequestBody BookingCreatetDto booking,
                                 @RequestHeader(USER_ID) long bookerId) {
        log.info("==>POST /bookings {}, bookerId={}", booking, bookerId);
        BookingFullDTO bookingDTO = bookingService.create(booking, bookerId);
        log.info("POST /bookings <== {}, bookerId={}", booking, bookerId);
        return bookingDTO;
    }

    @PatchMapping("/{bookingId}")
    public BookingFullDTO approved(@PathVariable(name = "bookingId") long bookingId,
                                   @RequestParam(value = "approved") boolean approved,
                                   @RequestHeader(USER_ID) long bookerId) {
        log.info("==>PATCH /bookings/{}?approved={} bookerId={}", bookingId, approved, bookerId);
        BookingParams params = BookingParams.builder()
                .id(bookingId)
                .userId(bookerId)
                .approved(approved)
                .build();
        BookingFullDTO bookingDto = bookingService.approved(params);
        log.info("PATCH <== /bookings/{}?approved={} bookerId={}", bookingId, approved, bookerId);
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingFullDTO getById(@PathVariable(name = "bookingId") long bookingId,
                                  @RequestHeader(USER_ID) long userId) {
        log.info("==>GET /bookings/{} by {}", bookingId, userId);
        BookingParams params = BookingParams.builder()
                .id(bookingId)
                .userId(userId)
                .build();
        BookingFullDTO bookingDto = bookingService.getById(params);
        log.info("GET /bookings <== {} by {}", bookingDto, userId);
        return bookingDto;
    }

    @GetMapping
    public List<BookingFullDTO> getBookings(@RequestParam(value = "state") BookingState stateParam,
                                            @RequestHeader(USER_ID) long userId) {
        log.info("==>GET /bookings/?state={} by {}", stateParam, userId);
        List<BookingFullDTO> bookingDto = bookingService.getUserBookings(stateParam, userId);
        log.info("GET /bookings <== {} by {}", bookingDto, userId);
        return bookingDto;
    }


    @GetMapping("/owner")
    public List<BookingFullDTO> getOwnerBookings(@RequestParam(value = "state") BookingState stateParam,
                                                 @RequestHeader(USER_ID) long userId) {
        log.info("==>GET /bookings/?state={} by {}", stateParam, userId);
        List<BookingFullDTO> bookingDto = bookingService.getOwnerBookings(stateParam, userId);
        log.info("GET /bookings <== {} by {}", bookingDto, userId);
        return bookingDto;
    }
}
