package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentCreateDTO;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentParams;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.dto.ItemUpdateDTO;
import ru.practicum.shareit.marker.Marker;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {

    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    @Validated
    public ItemFullDto getById(@PathVariable long itemId,
                               @NotNull @RequestHeader(USER_ID) long userId) {
        log.info("GET ==> /items");
        ItemFullDto saveItem = itemService.getById(itemId, userId);
        log.info("<== GET /items {}", saveItem);
        return saveItem;
    }

    @PostMapping("{itemId}/comment")
    @Validated
    public CommentDto createComment(@Valid @RequestBody CommentCreateDTO comment,
                                    @PathVariable long itemId,
                                    @NotNull @RequestHeader(USER_ID) long userId) {
        log.info("POST ==> /items/{}/comment Comment={}", itemId, comment);
        CommentParams params = CommentParams.builder()
                .authorId(userId)
                .text(comment.getText())
                .itemId(itemId)
                .build();
        CommentDto savedComment = itemService.createComment(params);
        log.info("<== POST /items/{}/comment Comment={}", itemId, savedComment);
        return savedComment;
    }

    @GetMapping
    @Validated
    public List<ItemFullDto> getAllOwnerItems(@RequestHeader(USER_ID) long ownerId) {
        log.info("GET ==> /items");
        List<ItemFullDto> savedItems = itemService.getAllOwnerItems(ownerId);
        log.info("<== GET /items {}", savedItems);
        return savedItems;
    }

    @PatchMapping("/{itemId}")
    @Validated({Marker.Update.class})
    public ItemShortDto update(@Valid @RequestBody ItemUpdateDTO item,
                               @PathVariable long itemId,
                               @NotNull @RequestHeader(USER_ID) long ownerId) {
        item.setId(itemId);
        log.info("PATCH ==> /items/{} {},ownerId={}", itemId, item, ownerId);

        ItemShortDto updateItem = itemService.update(item, ownerId);
        log.info("<== PATCH /items/{} {},ownerId={}", itemId, updateItem, ownerId);
        return updateItem;
    }

    @PostMapping
    @Validated({Marker.Create.class})
    public ItemShortDto create(@Valid @RequestBody ItemCreateDto item,
                               @RequestHeader(USER_ID) long ownerId) {
        log.info("==>POST /items {}, ownerId={}", item, ownerId);
        ItemShortDto createdItem = itemService.create(item, ownerId);
        log.info("POST /items <== {}, ownerId={}", item, ownerId);
        return createdItem;
    }

    @GetMapping("/search")
    @Validated
    public List<ItemShortDto> search(@RequestParam(name = "text") String text) {
        log.info("==>GET /search {}", text);
        return itemService.search(text);
    }
}
