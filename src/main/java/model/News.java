package model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class News {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(nullable = false)
  private String title;
  
  @Column(nullable = false)
  private String description;
  
  @Column(nullable = false)
  private LocalDateTime date;
  
  @ManyToOne
  private Game game;
  
  public News() {}
  
  public News(String title, String description, Game game) {
    this.title = title;
    this.description = description;
    this.game = game;
    date = LocalDateTime.now();
  }
  
  // JSON //
  
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
}
