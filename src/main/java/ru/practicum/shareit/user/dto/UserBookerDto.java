package ru.practicum.shareit.user.dto;

import java.util.Objects;

public class UserBookerDto {
    Long id;

    public UserBookerDto(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserBookerDto)) return false;
        UserBookerDto that = (UserBookerDto) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "UserBookerDto{" +
                "id=" + id +
                '}';
    }
}
