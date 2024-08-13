package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@Data
public class ItemUpdateDTO {
    private Long id;
    @Size(max = 100)
    private String name;
    @Size(max = 200)
    private String description;
    private Boolean available;
    private ItemRequest request;
}
