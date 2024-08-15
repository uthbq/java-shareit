package ru.practicum.shareit.request.dto;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@Component
public class ItemRequestMapper {

    public ItemRequest toItemRequest(ItemRequestDto requestDto) {
        return ItemRequest.builder()
                .requestor(requestDto.getRequestor())
                .created(requestDto.getCreated())
                .description(requestDto.getDescription())
                .build();
    }

    public ItemRequestShortDto toItemRequestShortDto(ItemRequest request) {
        return ItemRequestShortDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();
    }


    public ItemRequestFullDto toItemRequestFullDto(ItemRequest request, List<Item> items) {
        List<ItemRequestFullDto.Item> itemRecords = null;

        if (items != null) {
            itemRecords = items.stream().map(i ->
                    new ItemRequestFullDto.Item(i.getId(), i.getName(), i.getOwner().getId())).toList();
        }

        return ItemRequestFullDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(itemRecords)
                .build();
    }
}
