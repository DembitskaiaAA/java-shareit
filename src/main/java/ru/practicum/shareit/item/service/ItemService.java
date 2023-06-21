package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long owner, ItemDto itemDto);

    ItemDto updateItem(Long owner, Long itemId, ItemDto itemDto);

    ItemDto getItem(Long itemId, Long ownerId);

    List<ItemDto> getItems(Long owner);

    List<ItemDto> searchItems(String text);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);

    List<CommentDto> getCommentsByItemId(Long itemId);

    Item validItem(Long itemId);
}
