package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long owner, ItemDto itemDto);

    ItemDto updateItem(Long owner, long itemId, Item item);

    ItemDto getItem(long itemId);

    List<ItemDto> getItems(Long owner);

    List<ItemDto> searchItems(String text);
}
