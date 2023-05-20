package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class Item {
    Long id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    Boolean available;
    Long owner;
    Long request;

    public Item(String name, String description, Boolean available, Long owner) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }

}
