package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemUpdateDto {
    private Long id;
    @Size(max = 100)
    private String name;
    @Size(max = 200)
    private String description;
    private Boolean available;
}
