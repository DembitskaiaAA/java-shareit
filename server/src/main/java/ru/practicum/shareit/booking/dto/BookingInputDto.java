package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingInputDto {
    LocalDateTime start;
    LocalDateTime end;
    Long itemId;


/*    @NotNull(message = "Необходимо заполнить дату начала бронирования")
    @FutureOrPresent
    LocalDateTime start;
    @NotNull(message = "Необходимо заполнить дату окончания бронирования")
    @Future
    LocalDateTime end;
    @NotNull(message = "Необходимо указать id объекта бронирования")
    Long itemId;*/
}
