package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.validations.Create;
import ru.practicum.shareit.validations.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    Long id;
    @NotBlank(groups = {Create.class}, message = "Имя пользователя не может быть пустым")
    @Size(max = 50)
    String name;
    @NotBlank(groups = Create.class, message = "Email пользователя не может быть пустым")
    @Email(groups = {Create.class, Update.class}, regexp = "^[_A-Za-z0-9+-]+(?:[.'’][_A-Za-z0-9-]+)*@[_A-Za-z0-9-]+(?:\\.[_A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$",
            message = "Некорректно указан Email")
    String email;
}
