package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
public interface BookingService {
    BookingOutputDto createBooking(Long booker, BookingInputDto bookingInputDto);

    BookingOutputDto approveBooking(Long owner, Long bookingId, Boolean approved);

    BookingOutputDto getBooking(Long owner, Long bookingId);

    List<BookingOutputDto> getAllBookingByBookerId(Long booker, String state, Integer from, Integer size);

    List<BookingOutputDto> getAllBookingByOwnerId(Long owner, String state, Integer from, Integer size);

    List<BookingOutputDto> getBookingByState(List<Booking> savedBooking, String state);

    BookingItemDto getLastBooking(Item item, BookingStatus state);

    BookingItemDto getNextBooking(Item item, BookingStatus state);

    Booking getBookingByItemIdBookerIdForComment(Long itemId, Long userId);

    Booking validBooking(Long bookingId);
}
