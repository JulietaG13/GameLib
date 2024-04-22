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
  private String name;
  
  @Column(nullable = false)
  private String description;
  
  @Column(nullable = false)
  private LocalDateTime releaseDate;
  
  @Column(nullable = false)
  private LocalDateTime lastUpdate;
  
  //TODO(gameLogo, gamePicture, gameBanner)
  
  @ManyToMany(mappedBy = "games")
  private final Set<Shelf> inShelves = new HashSet<>();
  
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
  
  private Game(GameBuilder builder) {
    this.name = builder.name;
    this.description = builder.description;
    this.releaseDate = builder.releaseDate;
    this.lastUpdate = builder.lastUpdate;
  }
  
  public static GameBuilder create(String name) {
    return new GameBuilder(name);
  }
  
  public static class GameBuilder {
    private final String name;
    private String description;
    private LocalDateTime releaseDate;
    private LocalDateTime lastUpdate;
    
    public GameBuilder(String name) {
      this.name = name;
    }
    
    public GameBuilder description(String description) {
      this.description = description;
      return this;
    }
    
    public GameBuilder releaseDate(LocalDateTime releaseDate) {
      this.releaseDate = releaseDate;
      return this;
    }
    
    public GameBuilder lastUpdate(LocalDateTime lastUpdate) {
      this.lastUpdate = lastUpdate;
      return this;
    }
    
    public Game build() {
      if (description == null) {
        throw new IllegalArgumentException();
      }
      if (releaseDate == null) {
        releaseDate = LocalDateTime.now();
      }
      if (lastUpdate == null) {
        lastUpdate = LocalDateTime.now();
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
    jsonObj.addProperty("name", name);
    jsonObj.addProperty("description", description);
    jsonObj.addProperty("releaseDate", releaseDate.toString());
    jsonObj.addProperty("lastUpdate", lastUpdate.toString());
    return jsonObj.toString();
  }
  
  // RESTRICTIONS //
  
  public static MessageResponse isNameValid(String name) {
    if (name == null) {
      return new MessageResponse(true, "Name cannot be null!");
    }
    if (name.equals("")) {
      return new MessageResponse(true, "Name cannot be empty!");
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
  
  public String getName() {
    return name;
  }
  
  public void setName(String name, LocalDateTime lastUpdate) {
    this.name = name;
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
