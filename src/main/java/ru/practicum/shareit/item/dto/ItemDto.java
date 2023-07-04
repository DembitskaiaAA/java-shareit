package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.validations.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
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
}
