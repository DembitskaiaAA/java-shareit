package ru.practicum.shareit.user.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class User {
    long id;
    @NotBlank
    String name;
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(regexp = "^[_A-Za-z0-9+-]+(?:[.'’][_A-Za-z0-9-]+)*@[_A-Za-z0-9-]+(?:\\.[_A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$",
            message = "Некорректно указан Email")
    String email;
}
