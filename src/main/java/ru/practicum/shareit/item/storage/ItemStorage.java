package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item create(Item item);

    Item update(Item item, Long itemId, Long userId);

    Item getItemById(Long itemId);

    List<Item> getAllUserItems(Long id);

    List<Item> searchItem(String text);
}
