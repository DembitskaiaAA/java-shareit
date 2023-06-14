package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.UpdateItem;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
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
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long ownerId, @Valid @RequestBody Item item) {
        return itemService.createItem(ownerId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(required = true, name = "X-Sharer-User-Id") Long ownerId,
                              @PathVariable long itemId, @RequestBody UpdateItem updateItem) {
        return itemService.updateItem(ownerId, itemId, updateItem);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId,
                           @RequestHeader(required = true, name = "X-Sharer-User-Id") Long ownerId) {
        return itemService.getItem(itemId, ownerId);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(required = true, name = "X-Sharer-User-Id") Long ownerId) {
        return itemService.getItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(required = true) String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto,
                                    @RequestHeader(required = true, name = "X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId) {
        return itemService.createComment(commentDto, itemId, userId);
    }
}
