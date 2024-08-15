package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentParams;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.dto.ItemUpdateDTO;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    public static final String USER_ID = "X-Sharer-User-Id";
    public static final String BASE_URL = "/items";

    @MockBean
    private ItemService itemService;

    @Autowired
    MockMvc mockMvc;


    @Test
    void testGetItemById() throws Exception {
        long itemId = 1L;
        long userId = 1L;
        ItemFullDto responseItem = new ItemFullDto();

        when(itemService.getById(itemId, userId)).thenReturn(responseItem);
        mockMvc.perform(get(BASE_URL + "/{itemId}", itemId)
                        .header(USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseItem)));
        verify(itemService, times(1)).getById(itemId, userId);
        verify(itemService, atMost(1)).getById(anyLong(), anyLong());
    }

    @Test
    void testCreateComment() throws Exception {
        long itemId = 1L;
        long userId = 1L;
        CommentDto responseComment = new CommentDto();
        CommentCreateDto comment = new CommentCreateDto();
        CommentParams params = CommentParams.builder()
                .authorId(userId)
                .text(comment.getText())
                .itemId(itemId)
                .build();

        when(itemService.createComment(params)).thenReturn(responseComment);
        mockMvc.perform(post(BASE_URL + "/{itemId}/comment", itemId)
                        .header(USER_ID, userId)
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseComment)));
        verify(itemService, times(1)).createComment(params);
        verify(itemService, atMost(1)).createComment(any(CommentParams.class));
    }


    @Test
    void testGetAllOwnerItems() throws Exception {
        long userId = 1L;
        List<ItemFullDto> responseItem = Collections.emptyList();

        when(itemService.getAllOwnerItems(userId)).thenReturn(responseItem);
        mockMvc.perform(get(BASE_URL)
                        .header(USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseItem)));
        verify(itemService, times(1)).getAllOwnerItems(userId);
        verify(itemService, atMost(1)).getAllOwnerItems(anyLong());
    }

    @Test
    void testUpdateItem() throws Exception {
        long itemId = 1L;
        long userId = 1L;
        ItemShortDto responseItem = new ItemShortDto();
        ItemUpdateDTO itemUpdate = new ItemUpdateDTO();
        itemUpdate.setId(itemId);

        when(itemService.update(itemUpdate, userId)).thenReturn(responseItem);
        mockMvc.perform(patch(BASE_URL + "/{itemId}", itemId)
                        .header(USER_ID, userId)
                        .content(mapper.writeValueAsString(itemUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseItem)));
        verify(itemService, times(1)).update(itemUpdate, userId);
        verify(itemService, atMost(1)).update(any(ItemUpdateDTO.class), anyLong());
    }

    @Test
    void testCreateItem() throws Exception {
        long userId = 1L;
        ItemShortDto responseItem = new ItemShortDto();
        ItemCreateDto itemCreate = new ItemCreateDto();

        when(itemService.create(itemCreate, userId)).thenReturn(responseItem);
        mockMvc.perform(post(BASE_URL)
                        .header(USER_ID, userId)
                        .content(mapper.writeValueAsString(itemCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseItem)));
        verify(itemService, times(1)).create(itemCreate, userId);
        verify(itemService, atMost(1)).create(any(ItemCreateDto.class), anyLong());
    }

    @Test
    void search() throws Exception {
        long userId = 1L;
        String text = "test";
        List<ItemShortDto> responseItem = Collections.emptyList();

        when(itemService.search(text, userId)).thenReturn(responseItem);
        mockMvc.perform(get(BASE_URL + "/search")
                        .param("text", text)
                        .header(USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseItem)));
        verify(itemService, times(1)).search(text, userId);
        verify(itemService, atMost(1)).search(anyString(), anyLong());
    }
}