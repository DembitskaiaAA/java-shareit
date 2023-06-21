package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemBookerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserBookerDto;
import ru.practicum.shareit.user.model.User;

@Component
@Mapper(componentModel = "spring")
public abstract class BookingMapper {

    public abstract Booking transformBookingInputDtoToBooking(BookingInputDto bookingInputDto);

    @Mapping(target = "booker", expression = "java(mapToBooker(booking.getBooker()))")
    @Mapping(target = "item", expression = "java(mapToItem(booking.getItem()))")
    public abstract BookingOutputDto transformBookingToBookingOutputDto(Booking booking);

    UserBookerDto mapToBooker(User booker) {
        return new UserBookerDto(booker.getId());
    }

    ItemBookerDto mapToItem(Item item) {
        return new ItemBookerDto(item.getId(), item.getName());
    }

    @Mapping(target = "bookerId", expression = "java(mapToBookerId(booking.getBooker()))")
    public abstract BookingItemDto transformBookingToBookingItemDto(Booking booking);

    Long mapToBookerId(User booker) {
        return booker.getId();
    }
}
