package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserDto {
    long id;
    @NotBlank
    String name;
    @NotBlank
    String email;

    public UserDto(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDto)) return false;
        UserDto userDto = (UserDto) o;
        return getId() == userDto.getId() &&
                Objects.equals(getName(), userDto.getName()) &&
                Objects.equals(getEmail(), userDto.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getEmail());
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
