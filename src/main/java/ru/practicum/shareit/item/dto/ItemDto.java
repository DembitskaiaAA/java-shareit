package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    Boolean available;
    BookingItemDto lastBooking;
    BookingItemDto nextBooking;
    List<CommentDto> comments;

    public ItemDto(Long id, String name, String description, Boolean available,
                   BookingItemDto lastBooking, BookingItemDto nextBooking, List<CommentDto> comments) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.comments = comments;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getAvailable() {
        return available;
    }

    public BookingItemDto getLastBooking() {
        return lastBooking;
    }

    public BookingItemDto getNextBooking() {
        return nextBooking;
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemDto)) return false;
        ItemDto itemDto = (ItemDto) o;
        return Objects.equals(id, itemDto.id) && Objects.equals(name, itemDto.name) &&
                Objects.equals(description, itemDto.description) &&
                Objects.equals(available, itemDto.available) &&
                Objects.equals(lastBooking, itemDto.lastBooking) &&
                Objects.equals(nextBooking, itemDto.nextBooking) &&
                Objects.equals(comments, itemDto.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, available, lastBooking, nextBooking, comments);
    }

    @Override
    public String toString() {
        return "ItemDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", available=" + available +
                ", lastBooking=" + lastBooking +
                ", nextBooking=" + nextBooking +
                ", comments=" + comments +
                '}';
    }
}
