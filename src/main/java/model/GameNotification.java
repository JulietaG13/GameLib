package model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class GameNotification extends Notification {
  
  @ManyToOne
  private Game game;
  
  public GameNotification() {
    super();
  }
  
  public GameNotification(User owner, String description, Game game) {
    super(owner, description);
    this.game = game;
  }
  
  // GETTERS AND SETTERS
  
  public Game getGame() {
    return game;
  }
  
  public void setGame(Game game) {
    this.game = game;
  }
}
