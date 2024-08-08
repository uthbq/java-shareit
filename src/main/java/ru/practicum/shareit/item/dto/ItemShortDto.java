package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemShortDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest request;

    private List<Comment> comments;


}
