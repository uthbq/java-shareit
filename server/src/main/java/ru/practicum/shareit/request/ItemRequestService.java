package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestParams;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestShortDto create(ItemRequestDto itemRequestDto, long userId);

    List<ItemRequestFullDto> getUserRequests(long userId);

    List<ItemRequestFullDto> getAllRequest(ItemRequestParams params);

    ItemRequestFullDto getRequestById(long requestId, long userId);

}
