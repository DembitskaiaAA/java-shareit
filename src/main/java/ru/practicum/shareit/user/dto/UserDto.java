package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Data
public class UserDto {
    long id;
    @NotBlank
    String name;
    @NotBlank
    String email;
}
