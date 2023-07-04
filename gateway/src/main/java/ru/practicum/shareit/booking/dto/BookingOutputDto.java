package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemBookerDto;
import ru.practicum.shareit.user.dto.UserBookerDto;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingOutputDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    BookingStatus status;
    UserBookerDto booker;
    ItemBookerDto item;
}