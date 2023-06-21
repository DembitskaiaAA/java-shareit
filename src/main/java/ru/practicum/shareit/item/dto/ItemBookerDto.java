package ru.practicum.shareit.item.dto;

import java.util.Objects;

public class ItemBookerDto {
    Long id;
    String name;

    public ItemBookerDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemBookerDto)) return false;
        ItemBookerDto that = (ItemBookerDto) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    @Override
    public String toString() {
        return "ItemBookerDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
