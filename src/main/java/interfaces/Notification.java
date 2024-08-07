package interfaces;

import com.google.gson.JsonObject;
import model.Game;
import model.User;
import services.DateTimeService;

import java.time.LocalDateTime;

public interface Notification {
  
  Long getId();
  
  User getOwner();
  
  String getDescription();
  
  LocalDateTime getDateTime();
  
  default boolean isGameRelated() {
    return getGame() != null;
  }
  
  default Game getGame() {
    return null;
  }
  
  default JsonObject asJson() {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("id", getId());
    jsonObject.addProperty("owner_id", getOwner().getId());
    jsonObject.addProperty("description", getDescription());
    jsonObject.addProperty("date", DateTimeService.getDate(getDateTime()));
    jsonObject.addProperty("time", DateTimeService.getTime(getDateTime()));
    jsonObject.addProperty("is_game_related", isGameRelated());
    jsonObject.addProperty("game_id", getGame() == null ? null : getGame().getId());
    return jsonObject;
  }
}
