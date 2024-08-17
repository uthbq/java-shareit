package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemRequestParams {
    private Long userId;
    private Long from;
    private Long size;

}
