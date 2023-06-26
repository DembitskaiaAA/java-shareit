package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.validations.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
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
    @Positive(groups = {Create.class}, message = "Id запроса не может быть отрицательным")
    Long requestId;
    List<CommentDto> comments;
    BookingItemDto lastBooking;
    BookingItemDto nextBooking;

    public ItemDto() {
    }

    public ItemDto(Long id, String name, String description, Boolean available, Long requestId, List<CommentDto> comments, BookingItemDto lastBooking, BookingItemDto nextBooking) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
        this.comments = comments;
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
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

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemDto)) return false;
        ItemDto itemDto = (ItemDto) o;
        return Objects.equals(getId(), itemDto.getId()) && Objects.equals(getName(), itemDto.getName()) && Objects.equals(getDescription(), itemDto.getDescription()) && Objects.equals(getAvailable(), itemDto.getAvailable()) && Objects.equals(requestId, itemDto.requestId) && Objects.equals(getComments(), itemDto.getComments()) && Objects.equals(getLastBooking(), itemDto.getLastBooking()) && Objects.equals(getNextBooking(), itemDto.getNextBooking());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getAvailable(), requestId, getComments(), getLastBooking(), getNextBooking());
    }

    @Override
    public String toString() {
        return "ItemDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", available=" + available +
                ", requestId=" + requestId +
                ", comments=" + comments +
                ", lastBooking=" + lastBooking +
                ", nextBooking=" + nextBooking +
                '}';
    }
}
