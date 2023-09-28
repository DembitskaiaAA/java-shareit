package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.validations.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
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
}
