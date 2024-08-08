package ru.practicum.shareit.comment.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentParams {
    private String text;
    private Long authorId;
    private Long itemId;
    private LocalDateTime created;
}
