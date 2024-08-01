package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryItemStorage implements ItemStorage {
    private final HashMap<Long, Item> items = new HashMap<>();

    private static Long currentItemId = 0L;

    @Override
    public Item create(Item item) {
        item.setId(++currentItemId);
        log.info("Вещь с id={}, успешно создана",item.getId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item newItem, Long itemId, Long userId) {
        Item item = items.get(itemId);

        if (!item.getOwnerId().equals(userId)) {
            throw new NotFoundException("Вещь с таким id: " + itemId + " у пользователя " + userId + " не найдена");
        }

        if (newItem.getName() != null) {
            item.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            item.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            item.setAvailable(newItem.getAvailable());
        }
        log.info("Информация о вещи с id={}, успешно обновлена",itemId);
        return item;
    }

    @Override
    public Item getItemById(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Вещь с таким id: " + itemId + " не найдена");
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> getAllUserItems(Long id) {
        log.info("Список всех вещей пользователя с id={} получен успешно",id);
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItem(String text) {
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}

