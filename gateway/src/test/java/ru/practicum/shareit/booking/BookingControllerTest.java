package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    private static final String USER_ID = "X-Sharer-User-Id";

    private ResponseEntity<Object> response = new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
    @MockBean
    BookingClient bookingClient;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetAllPastBookings() throws Exception {
        String state = "PAST";
        long userId = 1L;

        when(bookingClient.getBookings(BookingState.PAST, userId)).thenReturn(response);
        mockMvc.perform(get("/bookings")
                        .param("state", state)
                        .header(USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(bookingClient, times(1)).getBookings(BookingState.PAST, userId);
    }

    @Test
    void testGetAllCurrentBookings() throws Exception {
        String state = "CURRENT";
        long userId = 1L;

        when(bookingClient.getBookings(BookingState.CURRENT, userId)).thenReturn(response);
        mockMvc.perform(get("/bookings")
                        .param("state", state)
                        .header(USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(bookingClient, times(1)).getBookings(BookingState.CURRENT, userId);
    }

    @Test
    void testGetAllFutureBookings() throws Exception {
        String state = "FUTURE";
        long userId = 1L;

        when(bookingClient.getBookings(BookingState.FUTURE, userId)).thenReturn(response);
        mockMvc.perform(get("/bookings")
                        .param("state", state)
                        .header(USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(bookingClient, times(1)).getBookings(BookingState.FUTURE, userId);
    }

    @Test
    void testGetAllRejectedBookings() throws Exception {
        String state = "REJECTED";
        long userId = 1L;

        when(bookingClient.getBookings(BookingState.REJECTED, userId)).thenReturn(response);
        mockMvc.perform(get("/bookings")
                        .param("state", state)
                        .header(USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(bookingClient, times(1)).getBookings(BookingState.REJECTED, userId);
    }

    @Test
    void testGetAllWaitingBookings() throws Exception {
        String state = "WAITING";
        long userId = 1L;

        when(bookingClient.getBookings(BookingState.WAITING, userId)).thenReturn(response);
        mockMvc.perform(get("/bookings")
                        .param("state", state)
                        .header(USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(bookingClient, times(1)).getBookings(BookingState.WAITING, userId);
    }

    @Test
    void testGetAllUnknownStateBookings() throws Exception {
        String state = "yesterday";
        long userId = 1L;

        mockMvc.perform(get("/bookings")
                        .param("state", state)
                        .header(USER_ID, userId))
                .andExpect(status().isInternalServerError());
        verify(bookingClient, never()).getBookings(any(BookingState.class), anyLong());
    }

}