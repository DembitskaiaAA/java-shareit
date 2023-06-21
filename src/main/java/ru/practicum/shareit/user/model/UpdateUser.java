package ru.practicum.shareit.user.model;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUser {

    long id;

    String name;

    @Email(regexp = "^[_A-Za-z0-9+-]+(?:[.'’][_A-Za-z0-9-]+)*@[_A-Za-z0-9-]+(?:\\.[_A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$",
            message = "Некорректно указан Email")
    String email;

    public UpdateUser() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpdateUser)) return false;
        UpdateUser that = (UpdateUser) o;
        return getId() == that.getId() &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getEmail(), that.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getEmail());
    }

    @Override
    public String toString() {
        return "UpdateUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
