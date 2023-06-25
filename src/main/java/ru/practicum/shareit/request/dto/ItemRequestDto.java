package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validations.Create;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ItemRequestDto {
    long id;
    @NotNull(groups = Create.class, message = "Необходимо заполнить описание в запросе")
    String description;
    @JsonIgnore
    Long requestor;
    LocalDateTime created;
    List<ItemDto> items;

    public ItemRequestDto() {
    }

    public ItemRequestDto(long id, String description, Long requestor, LocalDateTime created, List<ItemDto> items) {
        this.id = id;
        this.description = description;
        this.requestor = requestor;
        this.created = created;
        this.items = items;
    }
}
