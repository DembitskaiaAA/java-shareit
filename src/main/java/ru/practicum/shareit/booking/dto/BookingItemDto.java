package ru.practicum.shareit.booking.dto;

import java.util.Objects;

public class BookingItemDto {
    Long id;
    Long bookerId;

    public BookingItemDto(Long id, Long bookerId) {
        this.id = id;
        this.bookerId = bookerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookerId() {
        return bookerId;
    }

    public void setBookerId(Long bookerId) {
        this.bookerId = bookerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookingItemDto)) return false;
        BookingItemDto that = (BookingItemDto) o;
        return Objects.equals(id, that.id) && Objects.equals(bookerId, that.bookerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookerId);
    }

    @Override
    public String toString() {
        return "BookingItemDto{" +
                "id=" + id +
                ", bookerId=" + bookerId +
                '}';
    }
}
