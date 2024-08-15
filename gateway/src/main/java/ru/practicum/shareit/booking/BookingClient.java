package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingParams;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(BookingRequestDto requestDto, long bookerId) {
        return post("", bookerId, requestDto);
    }

    public ResponseEntity<Object> approvedBooking(BookingParams params) {
        Map<String, Object> parameters = Map.of(
                "bookingId", params.getId(),
                "approved", params.getApproved().toString()
        );
        return patch("/{bookingId}?approved={approved}", params.getUserId(), parameters, params);
    }

    public ResponseEntity<Object> getBooking(BookingParams params) {
        return get("/" + params.getId(), params.getUserId());
    }

    public ResponseEntity<Object> getBookings(BookingState state, long userId) {
        Map<String, Object> parameters = Map.of(
                "state", state.name()
        );
        return get("?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> getOwnerBookings(BookingState state, Long ownerId) {
        Map<String, Object> parameters = Map.of(
                "state", state.name()
        );
        return get("/owner?state={state}", ownerId, parameters);
    }

}
