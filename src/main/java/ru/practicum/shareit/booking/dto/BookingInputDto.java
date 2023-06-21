package ru.practicum.shareit.booking.dto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;


public class BookingInputDto {
    @NotNull(message = "Необходимо заполнить дату начала бронирования")
    @FutureOrPresent
    LocalDateTime start;
    @NotNull(message = "Необходимо заполнить дату окончания бронирования")
    @Future
    LocalDateTime end;
    @NotNull(message = "Необходимо указать id объекта бронирования")
    Long itemId;

    public BookingInputDto() {
    }

    public BookingInputDto(LocalDateTime start, LocalDateTime end, Long itemId) {
        this.start = start;
        this.end = end;
        this.itemId = itemId;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingInputDto that = (BookingInputDto) o;
        return Objects.equals(start, that.start) && Objects.equals(end, that.end) && Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, itemId);
    }

    @Override
    public String toString() {
        return "BookingInputDto{" +
                "start=" + start +
                ", end=" + end +
                ", itemId=" + itemId +
                '}';
    }
}
