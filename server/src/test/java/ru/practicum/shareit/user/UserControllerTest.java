package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @MockBean
    UserService userService;

    UserDto userDto;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testCreateUser() throws Exception {
        userDto = new UserDto(null, "John", "Doe");
        UserDto responseUser = new UserDto();

        when(userService.create(userDto)).thenReturn(responseUser);
        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseUser)));
        verify(userService, times(1)).create(userDto);
    }

    @Test
    public void testUpdateUser() throws Exception {
        long userId = 1L;
        userDto = new UserDto(userId, "John", "Doe");
        UserDto responseUser = new UserDto();
        when(userService.update(userDto)).thenReturn(responseUser);
        mockMvc.perform(patch("/users/{id}", userId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseUser)));
        verify(userService, times(1)).update(userDto);
    }

    @Test
    public void testDeleteUser() throws Exception {
        long userId = 1L;
        doNothing().when(userService).deleteUserById(userId);
        mockMvc.perform(delete("/users/{id}", userId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUserById(userId);
        verify(userService, atMost(1)).deleteUserById(anyLong());
    }

    @Test
    public void testGetAllUsers() throws Exception {
        when(userService.getAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(userService, times(1)).getAll();
    }

    @Test
    void testGetById() throws Exception {
        long userId = 1L;
        UserDto responseUser = new UserDto();

        when(userService.getById(userId)).thenReturn(responseUser);
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseUser)));
        verify(userService, times(1)).getById(userId);
    }

}
