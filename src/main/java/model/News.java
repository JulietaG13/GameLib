package model;

import com.google.gson.JsonObject;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
public class News {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(nullable = false)
  private String title;
  
  @Lob
  @Column(nullable = false, columnDefinition = "CLOB")
  private String description;
  
  @Column(nullable = false)
  private LocalDateTime date;
  
  @ManyToOne
  private Game game;
  
  @ManyToOne
  private User author;
  
  public News() {}
  
  public News(String title, String description, Game game, User author) {
    this.title = title;
    this.description = description;
    this.game = game;
    this.author = author;
    date = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
  }
  
  // JSON //
  
  public JsonObject asJson() {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("id", id);
    jsonObject.addProperty("title", title);
    jsonObject.addProperty("description", description);
    jsonObject.addProperty("date", date.toString());
    jsonObject.addProperty("game_id", game.getId());
    jsonObject.addProperty("author_id", author.getId());
    return jsonObject;
  }
  
  // GETTERS SETTERS //
  
  public Long getId() {
    return id;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public LocalDateTime getDate() {
    return date;
  }
  
  public void setDate(LocalDateTime date) {
    this.date = date;
  }
  
  public Game getGame() {
    return game;
  }
  
  public void setGame(Game game) {
    this.game = game;
  }
  
  public User getAuthor() {
    return author;
  }
  
  public void setAuthor(User author) {
    this.author = author;
  }
}
