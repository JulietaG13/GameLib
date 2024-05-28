package model;

import adapters.GsonAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entities.Rol;
import entities.responses.ErrorResponse;
import entities.responses.StatusResponse;
import example.ImageExample;
import interfaces.Responses;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne
    private User owner;

    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false)
    private LocalDate releaseDate;
    
    @Column(nullable = false)
    private LocalDate lastUpdate;

    //TODO(gameLogo, gameBanner)

    @ManyToMany(mappedBy = "games")
    private final Set<Shelf> inShelves = new HashSet<>();

    @Lob
    @Column(nullable = false, columnDefinition = "CLOB")
    private String cover = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAkACQAAD/2wBDAAIBAQIBAQICAgICAgICAwUDAwMDAwYEBAMFBwYHBwcGBwcICQsJCAgKCAcHCg0KCgsMDAwMBwkODw0MDgsMDAz/2wBDAQICAgMDAwYDAwYMCAcIDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAz/wAARCAAKAAoDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9BbX/AILufBvVz8d9WtPjx+zzH4Z8D2cUHhIXep39vqc+orBMLkXlu8Qe5t/tAgET6cs5dDJ/FtByvhb/AMHN/wCx/dfDLw5J4w+OnhWHxdJpds2tpo/hzX205L4xKbgWxmsllMIl37DIqvt27gDkV/OF/wAF2PCel+B/+CvXx90vRdM0/R9MtvFMphtLG3S3gi3Rxu21EAUZZmY4HJYnqa/qH/ZI/wCCXH7MviT9lL4Y6jqP7OnwJ1DUNQ8J6Vc3V1c+AdKlmuZXs4meR3aAlmZiSWJJJJJoA//Z";
    
    @Lob
    @Column(name = "background_image", nullable = false, columnDefinition = "CLOB")
    private String backgroundImage;
    //"https://i.pinimg.com/originals/05/ac/17/05ac17fb09440e9071908ef00efef134.png";
    
    @ManyToMany()
    @JoinTable(
        name = "games_tagged",
        joinColumns = @JoinColumn(name = "game_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private final Set<Tag> tags = new HashSet<>();

    @ManyToMany()
    @JoinTable(
            name = "games_upvoted",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private final Set<User> upvotes = new HashSet<>();


    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private final Set<Review> reviews = new HashSet<>();

    public Game() {}

    public Game(String name, User owner, String description, LocalDate releaseDate, String cover, String backgroundImage) {
        this.name = name;
        if (owner.getRol() == Rol.DEVELOPER) {
            this.owner = owner;
        }
        this.description = description;
        this.releaseDate = releaseDate;
        this.lastUpdate = LocalDate.now();
        this.cover = cover;
        this.backgroundImage = backgroundImage;
    }

    private Game(GameBuilder builder) {
        this.name = builder.name;
        this.owner = builder.owner;
        this.description = builder.description;
        this.releaseDate = builder.releaseDate;
        this.lastUpdate = builder.lastUpdate;
        this.cover = builder.cover;
        this.backgroundImage = builder.backgroundImage;
    }

    public static GameBuilder create(String name) {
        return new GameBuilder(name);
    }

    public static class GameBuilder {
        private String description;
        private User owner;
        private final String name;
        private LocalDate releaseDate;
        private LocalDate lastUpdate;
        private String cover;
        private String backgroundImage;

        public GameBuilder(String name) {
            this.name = name;
        }

        public GameBuilder description(String description) {
            this.description = description;
            return this;
        }

        public GameBuilder owner(User owner) {
            this.owner = owner;
            return this;
        }

        public GameBuilder releaseDate(LocalDate releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        public GameBuilder lastUpdate(LocalDate lastUpdate) {
            this.lastUpdate = lastUpdate;
            return this;
        }

        public GameBuilder cover(String cover) {
            this.cover = cover;
            return this;
        }
    
        public GameBuilder backgroundImage(String backgroundImage) {
            this.backgroundImage = backgroundImage;
            return this;
        }

        public Game build() {
            if (description == null) {
                description = "";
            }
            if (cover == null) {
                cover = ImageExample.LAZY_COOL_CAT.image;
            }
            if (backgroundImage == null) {
                backgroundImage = ImageExample.COMPUTER_CAT_BANNER_MAYBE.image;
            }
            if (releaseDate == null) {
                releaseDate = LocalDate.now();
            }
            if (lastUpdate == null) {
                lastUpdate = LocalDate.now();
            }
            return new Game(this);
        }
    }
    
    // JSON //
    
    public static Game fromJson(String json) {
        final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, GsonAdapter.getLocalDateAdapter()).create();
        return gson.fromJson(json, Game.class);
    }
    
    public JsonObject asJson() {
        JsonArray jsonArray = new JsonArray();
        for (Tag tag : tags) {
            jsonArray.add(tag.asJsonWithoutGames());
        }

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("id", id);
        jsonObj.addProperty("name", name);
        jsonObj.addProperty("description", description);
        jsonObj.addProperty("owner_id", (owner == null ? null : owner.getId()));
        jsonObj.addProperty("cover", cover);
        jsonObj.addProperty("background_image", backgroundImage);
        jsonObj.addProperty("releaseDate", releaseDate.toString());
        jsonObj.addProperty("lastUpdate", lastUpdate.toString());
        jsonObj.add("tags", jsonArray);
        return jsonObj;
    }
    
    // RESTRICTIONS //

    /*
    public static Responses isGamePictureValid(String cover) {
        if (cover == null) {
            return new ErrorResponse(404, "Game picture cannot be null!");
        }
        if (cover.isEmpty()) {
            return new ErrorResponse(404, "Game picture cannot be empty!");
        }
        return new StatusResponse(200);
    }
    */
    
    public static Responses isNameValid(String name) {
        if (name == null) {
            return new ErrorResponse(404, "Name cannot be null!");
        }
        if (name.isEmpty()) {
            return new ErrorResponse(404, "Name cannot be empty!");
        }
        return new StatusResponse(200);
    }
    
    public static Responses isDescriptionValid(String description) {
        if (description == null) {
            return new ErrorResponse(404, "Description cannot be null!");
        }
        return new StatusResponse(200);
    }

    public static Responses isReleaseDateValid(LocalDate releaseDate) {
        if (releaseDate == null) {
            return new ErrorResponse(404, "Release date cannot be null!");
        }
        return new StatusResponse(200);
    }
    
    // ADDS? //
    
    protected void addInShelf(Shelf shelf) {
        inShelves.add(shelf);
    }
    
    protected void removeFromShelf(Shelf shelf) {
        inShelves.remove(shelf);
    }
    
    public void addTag(Tag tag) {
        tags.add(tag);
        tag.addGame(this);
    }
    
    public void addReview(Review review) {
        reviews.add(review);
        review.setGame(this);
    }

    public void addUpvote(User user) {
        upvotes.add(user);
        if (!user.getUpvotedGames().contains(this)) {
            user.addGameUpvote(this);
        }
     }
    
    // GETTERS - SETTERS //
    
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover, LocalDate lastUpdate) {
        this.cover = cover;
        this.setLastUpdate(lastUpdate);
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name, LocalDate lastUpdate) {
        this.name = name;
        this.setLastUpdate(lastUpdate);
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description, LocalDate lastUpdate) {
        this.description = description;
        this.setLastUpdate(lastUpdate);
    }
    
    public LocalDate getReleaseDate() {
        return releaseDate;
    }
    
    public void setReleaseDate(LocalDate releaseDate, LocalDate lastUpdate) {
        this.releaseDate = releaseDate;
        this.setLastUpdate(lastUpdate);
    }
    
    public LocalDate getLastUpdate() {
        return lastUpdate;
    }
    
    public void setLastUpdate(LocalDate lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Set<Shelf> getInShelves() {
        return inShelves;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public Set<User> getUpvotes() {
        return upvotes;
    }

    // OTHERS //

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != Game.class) {
            return false;
        }
        return Objects.equals(this.id, ((Game) obj).id);
    }
}
