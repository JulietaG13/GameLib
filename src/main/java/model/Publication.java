package model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column
    private String description;

    @Column
    private LocalDateTime creationDate;

    @ManyToOne
    private User author;

    @ManyToMany()
    private final List<User> liked = new ArrayList<>();

    public Publication(String description) {
        this.description = description;
        this.creationDate = LocalDateTime.now();
    }

    public Publication() {}

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public User getAuthor() {
        return author;
    }

    public void isPublishedBy(User author) {
        this.author = author;
    }

    public void isLikedBy(User user) {
        liked.add(user);
    }

    public List<User> getLikes() {
        return liked;
    }
}
