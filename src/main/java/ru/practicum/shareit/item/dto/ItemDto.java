package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.validations.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

public class ItemDto {
    Long id;
    @NotBlank(groups = {Create.class}, message = "Имя товара не может быть пустым")
    String name;
    @NotBlank(groups = {Create.class}, message = "Описание товара не может быть пустым")
    String description;
    @NotNull(groups = {Create.class}, message = "Необходимо указать доступность товара для бронирования")
    Boolean available;
    List<CommentDto> comments;
    BookingItemDto lastBooking;
    BookingItemDto nextBooking;

    public ItemDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }

    public BookingItemDto getLastBooking() {
        return lastBooking;
    }

    public void setLastBooking(BookingItemDto lastBooking) {
        this.lastBooking = lastBooking;
    }

    public BookingItemDto getNextBooking() {
        return nextBooking;
    }

    public void setNextBooking(BookingItemDto nextBooking) {
        this.nextBooking = nextBooking;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemDto)) return false;
        ItemDto itemDto = (ItemDto) o;
        return Objects.equals(id, itemDto.id) && Objects.equals(name, itemDto.name) && Objects.equals(description, itemDto.description) && Objects.equals(available, itemDto.available) && Objects.equals(comments, itemDto.comments) && Objects.equals(lastBooking, itemDto.lastBooking) && Objects.equals(nextBooking, itemDto.nextBooking);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, available, comments, lastBooking, nextBooking);
    }

    @Override
    public String toString() {
        return "ItemDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", available=" + available +
                ", comments=" + comments +
                ", lastBooking=" + lastBooking +
                ", nextBooking=" + nextBooking +
                '}';
    }
}
