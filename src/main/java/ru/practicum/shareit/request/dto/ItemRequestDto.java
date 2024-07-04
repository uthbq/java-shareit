package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

public class ItemRequestDto {
    private Long id;
    private String description;
    private UserDto requestor;
    private LocalDateTime created;
}
