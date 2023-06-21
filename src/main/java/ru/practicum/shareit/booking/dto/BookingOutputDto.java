package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemBookerDto;
import ru.practicum.shareit.user.dto.UserBookerDto;

import java.time.LocalDateTime;
import java.util.Objects;

public class BookingOutputDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    BookingStatus status;
    UserBookerDto booker;
    ItemBookerDto item;

    public BookingOutputDto(Long id, LocalDateTime start, LocalDateTime end,
                            BookingStatus status, UserBookerDto booker, ItemBookerDto item) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.status = status;
        this.booker = booker;
        this.item = item;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public UserBookerDto getBooker() {
        return booker;
    }

    public void setBooker(UserBookerDto booker) {
        this.booker = booker;
    }

    public ItemBookerDto getItem() {
        return item;
    }

    public void setItem(ItemBookerDto item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookingOutputDto)) return false;
        BookingOutputDto that = (BookingOutputDto) o;
        return Objects.equals(id, that.id) && Objects.equals(start, that.start) &&
                Objects.equals(end, that.end) && status == that.status &&
                Objects.equals(booker, that.booker) && Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, start, end, status, booker, item);
    }

    @Override
    public String toString() {
        return "BookingOutputDto{" +
                "id=" + id +
                ", start=" + start +
                ", end=" + end +
                ", status=" + status +
                ", booker=" + booker +
                ", item=" + item +
                '}';
    }
}
