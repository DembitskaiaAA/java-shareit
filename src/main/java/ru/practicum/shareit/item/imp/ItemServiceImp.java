package ru.practicum.shareit.item.imp;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
public class ItemServiceImp implements ItemService {

    private final ItemStorage itemStorage;

    public ItemServiceImp(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }


    @Override
    public ItemDto createItem(Long owner, ItemDto itemDto) {
        return itemStorage.createItem(owner, itemDto);
    }

    @Override
    public ItemDto updateItem(Long owner, long itemId, Item item) {
        return itemStorage.updateItem(owner, itemId, item);
    }

    @Override
    public ItemDto getItem(long itemId) {
        return itemStorage.getItem(itemId);
    }

    @Override
    public List<ItemDto> getItems(Long owner) {
        return itemStorage.getItems(owner);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemStorage.searchItems(text);
    }
}
