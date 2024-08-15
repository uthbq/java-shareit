package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestParams;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private static final String USER_ID = "X-Sharer-User-Id";

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                    @NotNull @RequestHeader(USER_ID) long userId) {
        return itemRequestClient.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnRequests(@RequestHeader(USER_ID) long userId) {
        ResponseEntity<Object> dtos = itemRequestClient.getOwnRequests(userId);
        return dtos;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(USER_ID) long userId,
                                                 @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Long from,
                                                 @Positive @RequestParam(value = "size", defaultValue = "10") Long size) {
        ItemRequestParams params = new ItemRequestParams(userId, from, size);
        ResponseEntity<Object> dtos = itemRequestClient.getAllRequest(params);
        return dtos;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable long requestId,
                                          @RequestHeader(USER_ID) long userId) {
        ResponseEntity<Object> dtos = itemRequestClient.getItemRequestById(requestId, userId);
        return dtos;
    }
}
