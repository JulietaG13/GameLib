package model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    private String description;

    //TODO(gameLogo, gamePicture, gameBanner)

    @ManyToMany(mappedBy = "games")
    private final List<Shelf> inShelves = new ArrayList<>();

    public Game() {}

    private Game(GameBuilder builder) {
        this.title = builder.title;
        this.description = builder.description;
    }

    public static GameBuilder create(String title) {
        return new GameBuilder(title);
    }

    public static class GameBuilder {
        private final String title;
        private String description;

        public GameBuilder(String title) {
            this.title = title;
        }

        public GameBuilder description(String description) {
            this.description = description;
            return this;
        }

        public Game build() {
            if(description == null) {
                throw new IllegalArgumentException();
            }
            return new Game(this);
        }
    }

    public void addInShelf(Shelf shelf) {
        inShelves.add(shelf);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
