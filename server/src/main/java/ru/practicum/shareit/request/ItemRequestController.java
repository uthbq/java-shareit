package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestParams;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestShortDto create(@RequestBody ItemRequestDto requestDto,
                                      @RequestHeader(USER_ID) long userId) {
        ItemRequestShortDto shortDto = itemRequestService.create(requestDto, userId);
        return shortDto;
    }

    @GetMapping
    public List<ItemRequestFullDto> getOwnRequests(@RequestHeader(USER_ID) long userId) {
        List<ItemRequestFullDto> dtos = itemRequestService.getUserRequests(userId);
        return dtos;
    }

    @GetMapping("/all")
    public List<ItemRequestFullDto> getAllRequests(@RequestHeader(USER_ID) long userId,
                                                   @RequestParam(value = "from") int from,
                                                   @RequestParam(value = "size") int size) {
        ItemRequestParams params = new ItemRequestParams(userId, from, size);
        List<ItemRequestFullDto> dtos = itemRequestService.getAllRequest(params);
        return dtos;
    }

    @GetMapping("/{requestId}")
    public ItemRequestFullDto getById(@PathVariable long requestId,
                                      @RequestHeader(USER_ID) long userId) {
        ItemRequestFullDto dto = itemRequestService.getRequestById(requestId, userId);
        return dto;
    }
}
