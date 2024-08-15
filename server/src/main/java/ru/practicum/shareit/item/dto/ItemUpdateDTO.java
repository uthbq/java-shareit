package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@Data
public class ItemUpdateDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest request;
}
