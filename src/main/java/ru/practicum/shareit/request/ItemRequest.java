package ru.practicum.shareit.request;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ItemRequest {
    long id;
    String description;
    Long requestor;
    LocalDateTime created;
}
