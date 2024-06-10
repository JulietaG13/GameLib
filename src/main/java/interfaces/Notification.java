package interfaces;

import model.Game;
import model.User;

import java.time.LocalDateTime;

public interface Notification {
  
  User getOwner();
  
  String getDescription();
  
  LocalDateTime getDateTime();
  
  default boolean isGameRelated() {
    return false;
  }
  
  default Game getGame() {
    return null;
  }
}
