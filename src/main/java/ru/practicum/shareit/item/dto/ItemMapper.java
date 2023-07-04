package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public abstract class ItemMapper {
    @Autowired
    protected BookingService bookingService;
    @Autowired
    protected ItemService itemService;

    public abstract Item transformItemDtoToItem(ItemDto itemDto);

    @Mapping(target = "comments", expression = "java(mapToComments(item.getId(), itemService))")
    @Mapping(target = "requestId", expression = "java(mapToRequestId(item.getRequest()))")
    public abstract ItemDto transformItemToItemDto(Item item);

    @Mapping(target = "lastBooking", expression = "java(mapToLastBooking(item.getId(), bookingService))")
    @Mapping(target = "nextBooking", expression = "java(mapToNextBooking(item.getId(), bookingService))")
    @Mapping(target = "comments", expression = "java(mapToComments(item.getId(), itemService))")
    @Mapping(target = "requestId", expression = "java(mapToRequestId(item.getRequest()))")
    public abstract ItemDto transformItemToItemForOwnerDto(Item item);


    @Mapping(target = "authorName", expression = "java(mapToAuthorName(comment.getAuthor()))")
    public abstract CommentDto transformCommentToCommentDto(Comment comment);

    Long mapToRequestId(ItemRequest itemRequest) {
        if (itemRequest == null) {
            return null;
        }
        return itemRequest.getId();
    }

    public BookingItemDto mapToLastBooking(Long itemId, BookingService bookingService) {
        Item item = new Item();
        item.setId(itemId);
        return bookingService.getLastBooking(item, BookingStatus.APPROVED);
    }

    public BookingItemDto mapToNextBooking(Long itemId, BookingService bookingService) {
        Item item = new Item();
        item.setId(itemId);
        return bookingService.getNextBooking(item, BookingStatus.APPROVED);
    }

    public List<CommentDto> mapToComments(Long itemId, ItemService itemService) {
        return itemService.getCommentsByItemId(itemId);
    }

    public String mapToAuthorName(User user) {
        return user.getName();
    }
}
