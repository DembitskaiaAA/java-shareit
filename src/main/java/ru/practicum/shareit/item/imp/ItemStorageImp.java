package ru.practicum.shareit.item.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemStorageImp implements ItemStorage {
    private final UserStorage userStorage;
    Map<Long, Item> items = new HashMap<>();
    long id = 0;

    @Autowired
    public ItemStorageImp(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public ItemDto createItem(Long owner, ItemDto itemDto) {
        if (userStorage.getAllUsers().stream().noneMatch(x -> x.getId() == owner)) {
            throw new NotFoundException(
                    String.format("При добавлении товара ошибка: владелец товара c id: %s отсутствует", owner));
        }
        Item item = ItemMapper.toItem(itemDto, owner);
        item.setId(++id);
        items.put(item.getId(), item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long owner, long itemId, Item item) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException(
                    String.format("При обновлении товара ошибка: товар c id: %s отсутствует", itemId));
        }
        if (!items.get(itemId).getOwner().equals(owner)) {
            throw new NotFoundException(
                    String.format("При обновлении товара ошибка: некорректно указан id: %s владельца", owner));
        }
        Item resultItem = items.get(itemId);
        Optional.ofNullable(item.getName()).ifPresent(resultItem::setName);
        Optional.ofNullable(item.getDescription()).ifPresent(resultItem::setDescription);
        Optional.ofNullable(item.getAvailable()).ifPresent(resultItem::setAvailable);
        items.put(item.getId(), resultItem);
        return ItemMapper.toItemDto(resultItem);
    }

    @Override
    public ItemDto getItem(long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException(
                    String.format("При получении информации о товаре ошибка: товар c id: %s отсутствует", itemId));
        }
        return ItemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public List<ItemDto> getItems(Long owner) {
        return items.values().stream()
                .filter(x -> x.getOwner().equals(owner))
                .map(ItemMapper::toItemDto)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(x -> (x.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                        x.getName().toLowerCase().contains(text.toLowerCase())) && x.getAvailable())
                .map(ItemMapper::toItemDto)
                .distinct()
                .collect(Collectors.toList());
    }
}
