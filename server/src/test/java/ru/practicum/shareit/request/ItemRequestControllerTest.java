package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestParams;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {
    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    public static final String USER_ID = "X-Sharer-User-Id";
    public static final String BASE_URL = "/requests";
    @Autowired
    private MockMvc mvc;
    @MockBean
    ItemRequestService service;

    @Test
    void testCreateRequest() throws Exception {
        long userId = 1L;
        ItemRequestShortDto responseDto = new ItemRequestShortDto();
        ItemRequestDto requestDto = new ItemRequestDto();
        when(service.create(requestDto, userId)).thenReturn(responseDto);
        mvc.perform(post(BASE_URL)
                        .header(USER_ID, userId)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseDto)));
        verify(service, times(1)).create(requestDto, userId);
    }

    @Test
    void testGetOwnRequests() throws Exception {
        long userId = 1L;
        List<ItemRequestFullDto> responseDto = Collections.emptyList();
        ItemRequestDto requestDto = new ItemRequestDto();
        when(service.getUserRequests(userId)).thenReturn(responseDto);
        mvc.perform(get(BASE_URL)
                        .header(USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(service, times(1)).getUserRequests(userId);
    }

    @Test
    void testGetAllRequests() throws Exception {
        long userId = 1L;
        int from = 6;
        int size = 10;
        ItemRequestParams params = new ItemRequestParams(userId, from, size);
        List<ItemRequestFullDto> responseDto = Collections.emptyList();
        when(service.getAllRequest(params)).thenReturn(responseDto);
        mvc.perform(get(BASE_URL + "/all")
                        .header(USER_ID, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(service, times(1)).getAllRequest(params);
    }

    @Test
    void testGetRequestById() throws Exception {
        long userId = 1L;
        long requestId = 1L;

        ItemRequestFullDto responseDto = new ItemRequestFullDto();
        when(service.getRequestById(requestId, userId)).thenReturn(responseDto);
        mvc.perform(get(BASE_URL + "/{requestId}", requestId)
                        .header(USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseDto)));
        verify(service, times(1)).getRequestById(requestId, userId);
    }
}