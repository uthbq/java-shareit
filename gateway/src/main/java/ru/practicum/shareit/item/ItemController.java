package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentParams;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {

    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    @Validated
    public ResponseEntity<Object> getById(@PathVariable long itemId,
                                          @NotNull @RequestHeader(USER_ID) long userId) {
        log.info("GET ==> /items");
        ResponseEntity<Object> item = itemClient.getItem(itemId, userId);
        log.info("<== GET /items {}", item);
        return item;
    }

    @PostMapping("{itemId}/comment")
    @Validated
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentCreateDto comment,
                                                @PathVariable long itemId,
                                                @NotNull @RequestHeader(USER_ID) long userId) {
        log.info("POST ==> /items/{}/comment Comment={}", itemId, comment);
        CommentParams params = CommentParams.builder()
                .authorId(userId)
                .text(comment.getText())
                .itemId(itemId)
                .build();
        ResponseEntity<Object> savedComment = itemClient.createComment(params);
        log.info("<== POST /items/{}/comment Comment={}", itemId, savedComment);
        return savedComment;
    }

    @GetMapping
    @Validated
    public ResponseEntity<Object> getAllOwnerItems(@RequestHeader(USER_ID) long ownerId) {
        log.info("GET ==> /items");
        ResponseEntity<Object> savedItems = itemClient.getItems(ownerId);
        log.info("<== GET /items {}", savedItems);
        return savedItems;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Valid @RequestBody ItemUpdateDto item,
                                         @PathVariable long itemId,
                                         @NotNull @RequestHeader(USER_ID) long ownerId) {
        item.setId(itemId);
        log.info("PATCH ==> /items/{} {},ownerId={}", itemId, item, ownerId);

        ResponseEntity<Object> updateItem = itemClient.updateItem(item, ownerId);
        log.info("<== PATCH /items/{} {},ownerId={}", itemId, updateItem, ownerId);
        return updateItem;
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemCreateDto item,
                                         @RequestHeader(USER_ID) long ownerId) {
        log.info("==>POST /items {}, ownerId={}", item, ownerId);
        ResponseEntity<Object> createdItem = itemClient.createItem(item, ownerId);
        log.info("POST /items <== {}, ownerId={}", item, ownerId);
        return createdItem;
    }

    @GetMapping("/search")
    @Validated
    public ResponseEntity<Object> search(@RequestParam(name = "text") String text,
                                         @RequestHeader(USER_ID) long userId) {
        log.info("==>GET /search {}", text);
        return itemClient.searchItems(userId, text);
    }
}
