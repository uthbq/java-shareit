package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    ItemRequest toItemRequest(ItemRequestDto itemRequestDto);
}
