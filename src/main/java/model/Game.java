package model;

import com.google.gson.Gson;
import com.google.gson.annotations.JsonAdapter;

import javax.persistence.*;
import java.util.*;

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
    private final transient Set<Shelf> inShelves = new HashSet<>();

    public Game() {}

    private Game(GameBuilder builder) {
        this.title = builder.title;
        this.description = builder.description;
    }

    public static Game fromJson(String json) {
        final Gson gson = new Gson();
        return gson.fromJson(json, Game.class);
    }

    public String asJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
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
