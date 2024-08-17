package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentParams;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.dto.ItemUpdateDTO;

import java.util.List;


public interface ItemService {

    ItemFullDto getById(long itemId, long userId);

    ItemShortDto create(ItemCreateDto item, long ownerId);

    ItemShortDto update(ItemUpdateDTO item, long ownerId);

    List<ItemShortDto> search(String text, long userId);

    List<ItemFullDto> getAllOwnerItems(long userId);

    CommentDto createComment(CommentParams params);
}
