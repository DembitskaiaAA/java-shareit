package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingInputDto {
    @NotNull(message = "Необходимо заполнить дату начала бронирования")
    @FutureOrPresent
    LocalDateTime start;
    @NotNull(message = "Необходимо заполнить дату окончания бронирования")
    @Future
    LocalDateTime end;
    @NotNull(message = "Необходимо указать id объекта бронирования")
    Long itemId;
}
