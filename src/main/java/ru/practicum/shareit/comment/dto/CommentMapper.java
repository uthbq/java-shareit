package ru.practicum.shareit.comment.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Component
public class CommentMapper {

    public Comment mapToComment(CommentParams params, User user, Item item) {
        return Comment.builder()
                .text(params.getText())
                .author(user)
                .item(item)
                .created(params.getCreated())
                .build();
    }

    public CommentDto mapToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }
}
