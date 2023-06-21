package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Objects;


public class CommentDto {
    private Long id;
    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;
    @JsonIgnore
    private Item item;
    private String authorName;
    private LocalDateTime created;

    public CommentDto(Long id, String text, Item item, String authorName, LocalDateTime created) {
        this.id = id;
        this.text = text;
        this.item = item;
        this.authorName = authorName;
        this.created = created;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommentDto)) return false;
        CommentDto that = (CommentDto) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getText(), that.getText()) &&
                Objects.equals(getItem(), that.getItem()) && Objects.equals(getAuthorName(), that.getAuthorName()) &&
                Objects.equals(getCreated(), that.getCreated());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getText(), getItem(), getAuthorName(), getCreated());
    }

    @Override
    public String toString() {
        return "CommentDto{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", item=" + item +
                ", authorName='" + authorName + '\'' +
                ", created=" + created +
                '}';
    }
}
