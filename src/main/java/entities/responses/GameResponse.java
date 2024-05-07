package entities.responses;

import interfaces.Responses;
import model.Game;

public class GameResponse implements Responses {
  private final Game game;
  
  public GameResponse(Game game) {
    this.game = game;
  }

  @Override
  public Game getGame() {
    return game;
  }
}
