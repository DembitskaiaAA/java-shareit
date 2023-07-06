package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validations.Create;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @Validated(Create.class) @RequestBody ItemDto itemDto) {
        log.info("Creating item {}, userId={}", itemDto, ownerId);
        return itemClient.createItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                                             @PathVariable long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Update item item={}, ownerId={}, itemId={}", itemDto, ownerId, itemId);
        return itemClient.updateItem(ownerId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable long itemId,
                                          @RequestHeader(name = "X-Sharer-User-Id") Long ownerId) {
        log.info("Get item itemId={}, ownerId={}", itemId, ownerId);
        return itemClient.getItem(itemId, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                                           @RequestParam(defaultValue = "0") @Min(value = 0) Integer from,
                                           @RequestParam(defaultValue = "20", required = false) @Min(value = 1) Integer size) {
        log.info("Get items by userId={}, from={}, size={}", ownerId, from, size);
        return itemClient.getItems(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam() String text,
                                              @RequestParam(defaultValue = "0") @Min(value = 0) Integer from,
                                              @RequestParam(defaultValue = "20", required = false) @Min(value = 1) Integer size) {
        log.info("Search items text={}, from={}, size={}", text, from, size);
        return itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto commentDto,
                                                @RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                @PathVariable Long itemId) {
        log.info("Creating comment {}, userId={}, itemId={}", commentDto, userId, itemId);
        return itemClient.createComment(commentDto, itemId, userId);
    }

}
