package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemBookerDto;
import ru.practicum.shareit.user.dto.UserBookerDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static Booking toBooking(BookingInputDto bookingInputDto) {
        return new Booking(
                bookingInputDto.getStart(),
                bookingInputDto.getEnd()
        );
    }

    public static BookingOutputDto toBookingDto(Booking booking) {
        return new BookingOutputDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new UserBookerDto(booking.getBooker().getId()),
                new ItemBookerDto(booking.getItem().getId(),
                        booking.getItem().getName())
        );
    }

    public static BookingItemDto toBookingItemDto(Booking booking) {
        if (booking == null) {
            return null;
        } else {
            return new BookingItemDto(
                    booking.getId(),
                    booking.getBooker().getId()
            );
        }
    }
}
