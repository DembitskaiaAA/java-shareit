package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validations.Create;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    long id;
    @NotNull(groups = Create.class, message = "Необходимо заполнить описание в запросе")
    String description;
    @JsonIgnore
    Long requestor;
    LocalDateTime created;
    List<ItemDto> items;
}
