package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Long userId);


    ItemDto update(ItemDto itemDto, Long itemId, Long userId);

    ItemDto getItemById(Long itemId);


    List<ItemDto> getAllUserItems(Long id);


    List<ItemDto> searchItem(String text);
}
