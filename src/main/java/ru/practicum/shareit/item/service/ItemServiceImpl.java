package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        userService.getUserById(userId);
        Item item = itemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        return itemMapper.toItemDto(itemStorage.create(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        userService.getUserById(userId);
        Item item = itemMapper.toItem(itemDto);
        return itemMapper.toItemDto(itemStorage.update(item, itemId, userId));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return itemMapper.toItemDto(itemStorage.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getAllUserItems(Long id) {
        return itemStorage.getAllUserItems(id).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemStorage.searchItem(text).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }
}
