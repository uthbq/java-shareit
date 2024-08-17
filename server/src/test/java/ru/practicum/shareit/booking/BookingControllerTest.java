package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreatetDto;
import ru.practicum.shareit.booking.dto.BookingFullDTO;
import ru.practicum.shareit.booking.dto.BookingParams;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    public static final String USER_ID = "X-Sharer-User-Id";
    @MockBean
    BookingService bookingService;

    BookingCreatetDto bookingDto;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        long itemId = 1;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        bookingDto = new BookingCreatetDto(start, end, itemId, null);

    }

    @Test
    void create() throws Exception {
        long userId = 1L;
        when(bookingService.create(bookingDto, userId)).thenReturn(new BookingFullDTO());
        mockMvc.perform(post("/bookings")
                        .header(USER_ID, userId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new BookingFullDTO())));
        verify(bookingService, times(1)).create(bookingDto, userId);
    }

    @Test
    void testGetAllPastBookings() throws Exception {
        String state = "PAST";
        long userId = 1L;

        when(bookingService.getUserBookings(BookingState.PAST, userId)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/bookings")
                        .param("state", state)
                        .header(USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(bookingService, times(1)).getUserBookings(BookingState.PAST, userId);
    }

    @Test
    void testGetAllCurrentBookings() throws Exception {
        String state = "CURRENT";
        long userId = 1L;

        when(bookingService.getUserBookings(BookingState.CURRENT, userId)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/bookings")
                        .param("state", state)
                        .header(USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(bookingService, times(1)).getUserBookings(BookingState.CURRENT, userId);
    }

    @Test
    void testGetAllFutureBookings() throws Exception {
        String state = "FUTURE";
        long userId = 1L;

        when(bookingService.getUserBookings(BookingState.FUTURE, userId)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/bookings")
                        .param("state", state)
                        .header(USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(bookingService, times(1)).getUserBookings(BookingState.FUTURE, userId);
    }

    @Test
    void testGetAllRejectedBookings() throws Exception {
        String state = "REJECTED";
        long userId = 1L;

        when(bookingService.getUserBookings(BookingState.REJECTED, userId)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/bookings")
                        .param("state", state)
                        .header(USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(bookingService, times(1)).getUserBookings(BookingState.REJECTED, userId);
    }

    @Test
    void testGetAllWaitingBookings() throws Exception {
        String state = "WAITING";
        long userId = 1L;

        when(bookingService.getUserBookings(BookingState.WAITING, userId)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/bookings")
                        .param("state", state)
                        .header(USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(bookingService, times(1)).getUserBookings(BookingState.WAITING, userId);
    }

    @Test
    void testGetAllUnknownStateBookings() throws Exception {
        String state = "yesterday";
        long userId = 1L;

        mockMvc.perform(get("/bookings")
                        .param("state", state)
                        .header(USER_ID, userId))
                .andExpect(status().isInternalServerError());
        verify(bookingService, never()).getUserBookings(any(), any());
    }


    @Test
    void testGetById() throws Exception {
        long bookingId = 1L;
        long userId = 1L;
        BookingParams params = BookingParams.builder()
                .id(bookingId)
                .userId(userId)
                .build();

        when(bookingService.getById(params)).thenReturn(new BookingFullDTO());
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new BookingFullDTO())));
        verify(bookingService, times(1)).getById(params);
    }

    @Test
    void testGetOwnerBookings() throws Exception {

        BookingState bookingState = BookingState.CURRENT;
        String state = bookingState.toString();

        long userId = 1L;

        when(bookingService.getOwnerBookings(bookingState, userId)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/bookings/owner")
                        .param("state", state)
                        .header(USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(bookingService, times(1)).getOwnerBookings(bookingState, userId);
        verify(bookingService, atMostOnce()).getOwnerBookings(any(), any());
    }

    @Test
    void testGetAllUnknownStateOwnerBookings() throws Exception {
        String state = "yesterday";
        long userId = 1L;

        mockMvc.perform(get("/bookings")
                        .param("state", state)
                        .header(USER_ID, userId))
                .andExpect(status().isInternalServerError());
        verify(bookingService, never()).getOwnerBookings(any(), any());
    }


    @Test
    void testApprovedBooking() throws Exception {
        long userId = 1L;
        long bookingId = 1L;
        String approve = "true";
        BookingParams params = BookingParams.builder()
                .id(bookingId)
                .userId(userId)
                .approved(true)
                .build();
        when(bookingService.approved(params)).thenReturn(new BookingFullDTO());
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", approve)
                        .header(USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new BookingFullDTO())));
        verify(bookingService, times(1)).approved(params);
    }

    @Test
    void testNotApprovedBooking() throws Exception {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = false;
        BookingParams params = BookingParams.builder()
                .id(bookingId)
                .userId(userId)
                .approved(approved)
                .build();
        when(bookingService.approved(params)).thenReturn(new BookingFullDTO());
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", String.valueOf(approved))
                        .header(USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new BookingFullDTO())));

    }

}