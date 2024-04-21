package model;

import adapters.GsonAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import entities.responses.MessageResponse;

import javax.persistence.*;
import java.time.LocalDateTime;
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
    
    @Column(nullable = false)
    private LocalDateTime releaseDate;
    
    @Column(nullable = false)
    private LocalDateTime lastUpdate;

    //TODO(gameLogo, gamePicture, gameBanner)

    @ManyToMany(mappedBy = "games")
    private final Set<Shelf> inShelves = new HashSet<>();
    
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "games_tagged",
        joinColumns = @JoinColumn(name = "game_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private final Set<Tag> tags = new HashSet<>();

    public Game() {}

    private Game(GameBuilder builder) {
        this.title = builder.title;
        this.description = builder.description;
        this.releaseDate = builder.releaseDate;
        this.lastUpdate = builder.releaseDate;
    }

    public static GameBuilder create(String title) {
        return new GameBuilder(title);
    }

    public static class GameBuilder {
        private final String title;
        private String description;
        private LocalDateTime releaseDate;

        public GameBuilder(String title) {
            this.title = title;
        }

        public GameBuilder description(String description) {
            this.description = description;
            return this;
        }
        
        public GameBuilder releaseDate(LocalDateTime releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        public Game build() {
            if (description == null) {
                throw new IllegalArgumentException();
            }
            if (releaseDate == null) {
                releaseDate = LocalDateTime.now();
            }
            return new Game(this);
        }
    }
    
    // JSON //
    
    public static Game fromJson(String json) {
        final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, GsonAdapter.getLocalDateTimeAdapter()).create();
        return gson.fromJson(json, Game.class);
    }
    
    public String asJson() {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("id", id);
        jsonObj.addProperty("title", title);
        jsonObj.addProperty("description", description);
        jsonObj.addProperty("releaseDate", releaseDate.toString());
        jsonObj.addProperty("lastUpdate", lastUpdate.toString());
        return jsonObj.toString();
    }
    
    // RESTRICTIONS //
    
    public static MessageResponse isTitleValid(String title) {
        if (title == null) {
            return new MessageResponse(true, "Title cannot be null!");
        }
        if (title.equals("")) {
            return new MessageResponse(true, "Title cannot be empty!");
        }
        return new MessageResponse(false);
    }
    
    public static MessageResponse isDescriptionValid(String description) {
        if (description == null) {
            return new MessageResponse(true, "Description cannot be null!");
        }
        return new MessageResponse(false);
    }

    public static MessageResponse isReleaseDateValid(LocalDateTime releaseDate) {
        if (releaseDate == null) {
            return new MessageResponse(true, "Release date cannot be null!");
        }
        return new MessageResponse(false);
    }
    
    // ADDS? //
    
    protected void addInShelf(Shelf shelf) {
        inShelves.add(shelf);
    }
    
    public void addTag(Tag tag) {
        tags.add(tag);
        tag.addGame(this);
    }
    
    // GETTERS - SETTERS //
    
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title, LocalDateTime lastUpdate) {
        this.title = title;
        this.lastUpdate = lastUpdate;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description, LocalDateTime lastUpdate) {
        this.description = description;
        this.lastUpdate = lastUpdate;
    }
    
    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }
    
    public void setReleaseDate(LocalDateTime releaseDate, LocalDateTime lastUpdate) {
        this.releaseDate = releaseDate;
        this.lastUpdate = lastUpdate;
    }
    
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }
    
    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
