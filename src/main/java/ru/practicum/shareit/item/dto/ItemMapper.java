package ru.practicum.shareit.item.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

@Component
public class ItemMapper {

    private static BookingService bookingService;
    private static ItemService itemService;

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                itemService.getCommentsByItemId(item.getId())
        );
    }

    public static ItemDto toItemBookingDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                bookingService.getLastBooking(item, BookingStatus.APPROVED),
                bookingService.getNextBooking(item, BookingStatus.APPROVED),
                itemService.getCommentsByItemId(item.getId())
        );
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    private static void setBookingService(BookingService bookingService, ItemService itemService) {
        ItemMapper.bookingService = bookingService;
        ItemMapper.itemService = itemService;
    }

    public static BookingService getBookingService() {
        return bookingService;
    }

    public static ItemService getItemService() {
        return itemService;
    }

    @Autowired
    public void init(BookingService bookingService, ItemService itemService) {
        ItemMapper.setBookingService(bookingService, itemService);
    }
}
