package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentParams;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.dto.ItemUpdateDTO;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping("/{itemId}")

    public ItemFullDto getById(@PathVariable long itemId,
                               @RequestHeader(USER_ID) long userId) {
        log.info("GET ==> /items");
        ItemFullDto saveItem = itemService.getById(itemId, userId);
        log.info("<== GET /items {}", saveItem);
        return saveItem;
    }

    @PostMapping("/{itemId}/comment")

    public CommentDto createComment(@RequestBody CommentCreateDto comment,
                                    @PathVariable long itemId,
                                    @RequestHeader(USER_ID) long userId) {
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

    public List<ItemFullDto> getAllOwnerItems(@RequestHeader(USER_ID) long ownerId) {
        log.info("GET ==> /items");
        List<ItemFullDto> savedItems = itemService.getAllOwnerItems(ownerId);
        log.info("<== GET /items {}", savedItems);
        return savedItems;
    }

    @PatchMapping("/{itemId}")
    public ItemShortDto update(@RequestBody ItemUpdateDTO item,
                               @PathVariable long itemId,
                               @RequestHeader(USER_ID) long ownerId) {
        item.setId(itemId);
        log.info("PATCH ==> /items/{} {},ownerId={}", itemId, item, ownerId);

        ItemShortDto updateItem = itemService.update(item, ownerId);
        log.info("<== PATCH /items/{} {},ownerId={}", itemId, updateItem, ownerId);
        return updateItem;
    }

    @PostMapping
    public ItemShortDto create(@RequestBody ItemCreateDto item,
                               @RequestHeader(USER_ID) long ownerId) {
        log.info("==>POST /items {}, ownerId={}", item, ownerId);
        ItemShortDto createdItem = itemService.create(item, ownerId);
        log.info("POST /items <== {}, ownerId={}", item, ownerId);
        return createdItem;
    }

    @GetMapping("/search")

    public List<ItemShortDto> search(@RequestParam(name = "text") String text,
                                     @RequestHeader(USER_ID) long userId) {
        log.info("==>GET /search?text={}", text);
        return itemService.search(text, userId);
    }
}
