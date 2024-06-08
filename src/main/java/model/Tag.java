package model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import values.TagType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Tag {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;
  
  @Column(nullable = false, unique = true)
  private String name;
  
  @ManyToMany(mappedBy = "tags")
  private final Set<Game> taggedGames = new HashSet<>();

  @Column(nullable = false)
  private TagType tagType;
  
  public Tag() {}
  
  public Tag(String name) {
    this.name = name;
    this.tagType = TagType.OTHER;
  }

  public Tag(String name, TagType tagType) {
    this.name = name;
    this.tagType = tagType;
  }
  
  // JSON //
  
  public JsonObject asJson() {
    JsonArray jsonArray = new JsonArray();
    for (Game game : taggedGames) {
      JsonObject jsonGame = game.asJson();
      jsonArray.add(jsonGame);
    }
    JsonObject jsonObj = new JsonObject();
    jsonObj.addProperty("id", id);
    jsonObj.addProperty("name", name);
    jsonObj.addProperty("tag_type", tagType.name());
    jsonObj.add("tagged_games", jsonArray);
    return jsonObj;
  }

  public JsonObject asJsonWithoutGames() {
    JsonObject jsonObj = new JsonObject();
    jsonObj.addProperty("id", id);
    jsonObj.addProperty("name", name);
    jsonObj.addProperty("tag_type", tagType.name());
    return jsonObj;
  }
  
  // ADDS? //
  
  protected void addGame(Game game) {
    taggedGames.add(game);
  }
  
  // GETTERS SETTERS //
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public Long getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public Set<Game> getTaggedGames() {
    return taggedGames;
  }
}
