package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingParams;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody BookingRequestDto booking,
                                         @RequestHeader(USER_ID) long bookerId) {
        log.info("==>POST /bookings {}, bookerId={}", booking, bookerId);
        ResponseEntity<Object> createdBooking = bookingClient.createBooking(booking, bookerId);
        log.info("POST /bookings <== {}, bookerId={}", createdBooking, bookerId);
        return createdBooking;
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approved(@PathVariable(name = "bookingId") long bookingId,
                                           @RequestParam(value = "approved") boolean approved,
                                           @RequestHeader(USER_ID) long bookerId) {
        log.info("==>PATCH /bookings/{}?approved={} bookerId={}", bookingId, approved, bookerId);
        BookingParams params = BookingParams.builder()
                .id(bookingId)
                .userId(bookerId)
                .approved(approved)
                .build();
        ResponseEntity<Object> bookingDto = bookingClient.approvedBooking(params);
        log.info("PATCH <== /bookings/{}?approved={} bookerId={}", bookingId, approved, bookerId);
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@PathVariable(name = "bookingId") long bookingId,
                                          @RequestHeader(USER_ID) long userId) {
        log.info("==>GET /bookings/{} by {}", bookingId, userId);
        BookingParams params = BookingParams.builder()
                .id(bookingId)
                .userId(userId)
                .build();
        ResponseEntity<Object> bookingDto = bookingClient.getBooking(params);
        log.info("GET /bookings <== {} by {}", bookingDto, userId);
        return bookingDto;
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestParam(value = "state", defaultValue = "ALL") String stateParam,
                                              @RequestHeader(USER_ID) long userId) {
        log.info("==>GET /bookings/?state={} by {}", stateParam, userId);

        BookingState state = checkBookingState(stateParam);
        ResponseEntity<Object> bookingDto = bookingClient.getBookings(state, userId);
        log.info("GET /bookings <== {} by {}", bookingDto, userId);
        return bookingDto;
    }

    private static BookingState checkBookingState(String stateParam) {
        BookingState state = BookingState.from(stateParam);
        if (state == null) {
            throw new IllegalArgumentException("Unknown state: " + stateParam);
        }
        return state;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestParam(value = "state", defaultValue = "ALL") String stateParam,
                                                   @RequestHeader(USER_ID) long userId) {
        log.info("==>GET /bookings/?state={} by {}", stateParam, userId);

        BookingState state = checkBookingState(stateParam);
        ResponseEntity<Object> bookingDto = bookingClient.getOwnerBookings(state, userId);
        log.info("GET /bookings <== {} by {}", bookingDto, userId);
        return bookingDto;
    }


}
