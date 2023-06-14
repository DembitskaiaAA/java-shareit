package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.UpdateItem;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long owner, Item item);

    ItemDto updateItem(Long owner, Long itemId, UpdateItem updateItem);

    ItemDto getItem(Long itemId, Long ownerId);

    List<ItemDto> getItems(Long owner);

    List<ItemDto> searchItems(String text);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);

    List<CommentDto> getCommentsByItemId(Long itemId);
}
