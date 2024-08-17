package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestFullDto {

    private Long id;
    private String description;
    private LocalDateTime created;
    public List<Item> items;

    public record Item(Long itemId, String name, Long userId) {

    }
}
