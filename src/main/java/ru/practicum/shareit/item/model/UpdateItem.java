package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateItem {

    Long id;

    String name;

    String description;

    Boolean available;

    private User owner;

    Long request;

    public UpdateItem(Long id, String name, String description, Boolean available, User owner, Long request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = request;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Long getRequest() {
        return request;
    }

    public void setRequest(Long request) {
        this.request = request;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpdateItem)) return false;
        UpdateItem that = (UpdateItem) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getAvailable(), that.getAvailable()) && Objects.equals(getOwner(), that.getOwner()) && Objects.equals(getRequest(), that.getRequest());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getAvailable(), getOwner(), getRequest());
    }

    @Override
    public String toString() {
        return "UpdateItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", available=" + available +
                ", owner=" + owner +
                ", request=" + request +
                '}';
    }
}
