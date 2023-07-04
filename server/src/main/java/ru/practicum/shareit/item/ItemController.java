package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Component
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @RequestBody ItemDto itemDto) {
        return itemService.createItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(ownerId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId,
                           @RequestHeader(name = "X-Sharer-User-Id") Long ownerId) {
        return itemService.getItem(itemId, ownerId);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                                  @RequestParam Integer from,
                                  @RequestParam Integer size) {
        return itemService.getItems(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam() String text,
                                     @RequestParam Integer from,
                                     @RequestParam Integer size) {
        return itemService.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto,
                                    @RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId) {
        return itemService.createComment(commentDto, itemId, userId);
    }









/*    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @Validated(Create.class) @RequestBody ItemDto itemDto) {
        return itemService.createItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                              @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        return itemService.updateItem(ownerId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId,
                           @RequestHeader(name = "X-Sharer-User-Id") Long ownerId) {
        return itemService.getItem(itemId, ownerId);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                                  @RequestParam(defaultValue = "0") @Min(value = 0) Integer from,
                                  @RequestParam(defaultValue = "20", required = false) @Min(value = 1) Integer size) {
        return itemService.getItems(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam() String text,
                                     @RequestParam(defaultValue = "0") @Min(value = 0) Integer from,
                                     @RequestParam(defaultValue = "20", required = false) @Min(value = 1) Integer size) {
        return itemService.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto,
                                    @RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId) {
        return itemService.createComment(commentDto, itemId, userId);
    }*/
}
