package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

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
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long owner, @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(owner, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(required = true, name = "X-Sharer-User-Id") Long owner,
                              @PathVariable long itemId, @RequestBody Item item) {
        return itemService.updateItem(owner, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(required = true, name = "X-Sharer-User-Id") Long owner) {
        return itemService.getItems(owner);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(required = true) String text) {
        return itemService.searchItems(text);
    }
}
