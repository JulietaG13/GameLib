package model;

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
  
  public Tag() {}
  
  public Tag(String name) {
    this.name = name;
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
