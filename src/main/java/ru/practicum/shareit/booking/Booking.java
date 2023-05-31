package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class Booking {
    long id;
    @NotNull
    LocalDateTime start;
    @NotNull
    LocalDateTime end;
    @NotNull
    Long item;
    @NotNull
    Long booker;
    BookingStatus status;
}
