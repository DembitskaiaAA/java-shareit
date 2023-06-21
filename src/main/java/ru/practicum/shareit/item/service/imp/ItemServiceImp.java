package ru.practicum.shareit.item.service.imp;

import org.springframework.context.annotation.Lazy;
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
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ItemServiceImp implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingService bookingService;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;

    public ItemServiceImp(ItemRepository itemRepository, UserService userService, BookingService bookingService, CommentRepository commentRepository, @Lazy ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.bookingService = bookingService;
        this.commentRepository = commentRepository;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemDto createItem(Long owner, ItemDto itemDto) {
        User savedUser = userService.validUser(owner);
        Item savedItem = itemMapper.transformItemDtoToItem(itemDto);
        savedItem.setOwner(savedUser);
        Item item = itemRepository.save(savedItem);
        return itemMapper.transformItemToItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long owner, Long itemId, ItemDto itemDto) {
        Item item = validItem(itemId);
        User user = userService.validUser(owner);

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        item.setOwner(user);

        return itemMapper.transformItemToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItem(Long itemId, Long ownerId) {
        Item item = validItem(itemId);
        if (item.getOwner().getId() != ownerId) {
            return itemMapper.transformItemToItemDto(item);
        } else {
            return itemMapper.transformItemToItemForOwnerDto(item);
        }
    }

    @Override
    public List<ItemDto> getItems(Long ownerId) {
        userService.validUser(ownerId);
        return itemRepository.findAllByOwnerId(ownerId).stream()
                .map(itemMapper::transformItemToItemForOwnerDto).sorted(Comparator.comparing(ItemDto::getId)).collect(toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        List<Item> savedItemsByName = itemRepository.search(text);

        return savedItemsByName.stream().map(itemMapper::transformItemToItemDto).collect(toList());
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        userService.validUser(userId);
        itemRepository.findById(userId).orElseThrow(() -> new NotAvailableException(
                String.format("При создании комментария ошибка: неверно передан id: %s товара", itemId)));
        Comment comment = new Comment();
        Booking booking = bookingService.getBookingByItemIdBookerIdForComment(itemId, userId);
        if (booking != null) {
            comment.setCreated(LocalDateTime.now());
            comment.setItem(booking.getItem());
            comment.setAuthor(booking.getBooker());
            comment.setText(commentDto.getText());
        } else {
            throw new NotAvailableException(String.format("При создании комментария ошибка: бронирование товара c id: %s пользователем c id: %s отсутствует", itemId, userId));
        }
        return itemMapper.transformCommentToCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getCommentsByItemId(Long itemId) {
        return commentRepository.findAllByItemId(itemId,
                        Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(itemMapper::transformCommentToCommentDto)
                .collect(toList());
    }

    @Override
    public Item validItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(
                String.format("При получении товара ошибка: товар c id: %s отсутствует", itemId)));
    }


}
