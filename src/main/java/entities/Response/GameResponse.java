package entities.Response;

import interfaces.Response;
import model.Game;

public class GameResponse implements Response {
  private final boolean hasError;
  private final Game game;
  private final String message;
  
  public GameResponse(boolean hasError, Game game, String message) {
    this.hasError = hasError;
    this.message = message;
    this.game = game;
  }
  
  public GameResponse(boolean hasError, String message) {
    this.hasError = hasError;
    this.message = message;
    this.game = null;
  }
  
  @Override
  public boolean hasError() {
    return hasError;
  }
  
  @Override
  public String getMessage() {
    return message;
  }
  
  public Game getGame() {
    return game;
  }
}
