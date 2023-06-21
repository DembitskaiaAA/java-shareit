package ru.practicum.shareit.item.model;

import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "COMMENTS")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;
    @ManyToOne()
    @JoinColumn(name = "ITEM_ID", referencedColumnName = "ID")
    private Item item;
    @ManyToOne()
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    private User author;
    private LocalDateTime created;

    public Comment() {
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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
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
        if (!(o instanceof Comment)) return false;
        Comment comment = (Comment) o;
        return Objects.equals(getId(), comment.getId()) &&
                Objects.equals(getText(), comment.getText()) &&
                Objects.equals(getItem(), comment.getItem()) &&
                Objects.equals(getAuthor(), comment.getAuthor()) &&
                Objects.equals(getCreated(), comment.getCreated());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getText(), getItem(), getAuthor(), getCreated());
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", item=" + item +
                ", author=" + author +
                ", created=" + created +
                '}';
    }
}
