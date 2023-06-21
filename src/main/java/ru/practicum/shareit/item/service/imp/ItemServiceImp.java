package ru.practicum.shareit.item.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.UpdateItem;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ItemServiceImp implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingService bookingService;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImp(ItemRepository itemRepository, UserRepository userRepository, BookingService bookingService, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingService = bookingService;
        this.commentRepository = commentRepository;
    }


    @Override
    public ItemDto createItem(Long owner, Item item) {
        User savedUser = userRepository.findById(owner).orElseThrow(() -> new NotFoundException(
                String.format("При добавлении товара ошибка: владелец товара c id: %s отсутствует", owner)));
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long owner, Long itemId, UpdateItem updateItem) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(
                String.format("При получении товара ошибка: товар c id: %s отсутствует", itemId)));
        User user = userRepository.findById(owner).orElseThrow(() -> new NotFoundException(
                String.format("При добавлении товара ошибка: владелец товара c id: %s отсутствует", owner)));

        if (updateItem.getName() != null) {
            item.setName(updateItem.getName());
        }

        if (updateItem.getDescription() != null) {
            item.setDescription(updateItem.getDescription());
        }

        if (updateItem.getAvailable() != null) {
            item.setAvailable(updateItem.getAvailable());
        }

        if (updateItem.getOwner() != null) {
            item.setOwner(user);
        }

        if (updateItem.getRequest() != null) {
            item.setRequest(updateItem.getRequest());
        }

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItem(Long itemId, Long ownerId) {
        Item savedItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(
                String.format("При получении товара ошибка: товар c id: %s отсутствует", itemId)));
        if (savedItem.getOwner().getId() != ownerId) {
            return ItemMapper.toItemDto(savedItem);
        } else {
            return ItemMapper.toItemBookingDto(savedItem);
        }
    }

    @Override
    public List<ItemDto> getItems(Long ownerId) {
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException(
                String.format("При получении товаров владельца ошибка: владелец c id: %s отсутствует", ownerId)));
        List<ItemDto> result = itemRepository.findAllByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemBookingDto).sorted(Comparator.comparing(ItemDto::getId)).collect(toList());
        return result;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        List<Item> savedItemsByName = itemRepository.search(text);

        return savedItemsByName.stream().map(ItemMapper::toItemDto).collect(toList());
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                String.format("При создании комментария ошибка: пользователь c id: %s отсутствует", userId)));
        itemRepository.findById(userId).orElseThrow(() -> new NotAvailableException(
                String.format("При создании комментария ошибка: товар c id: %s отсутствует", itemId)));
        Comment comment = new Comment();
        Booking booking = bookingService.getBookingByUserIdItemIdForComment(itemId, userId);
        if (booking != null) {
            comment.setCreated(LocalDateTime.now());
            comment.setItem(booking.getItem());
            comment.setAuthor(booking.getBooker());
            comment.setText(commentDto.getText());
        } else {
            throw new NotAvailableException(String.format("При создании комментария ошибка: бронирование товара c id: %s пользователем c id: %s отсутствует", itemId, userId));
        }
        return ItemMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getCommentsByItemId(Long itemId) {
        return commentRepository.findAllByItemId(itemId,
                        Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(ItemMapper::toCommentDto)
                .collect(toList());
    }
}
