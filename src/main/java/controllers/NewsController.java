package controllers;

import com.google.gson.JsonArray;
import entities.ErrorMessages;
import interfaces.Controller;
import model.Game;
import model.News;
import repositories.GameRepository;
import repositories.NewsRepository;
import spark.Spark;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;

public class NewsController implements Controller {
  
  private static final String ROUTE_GET_FROM_GAME = "/news/get/game/:game_id";
  
  private EntityManagerFactory factory;
  private static Controller instance;
  
  private NewsController(EntityManagerFactory factory) {
    this.factory = factory;
  }
  
  public static Controller getInstance(EntityManagerFactory factory) {
    if (instance == null) {
      instance = new NewsController(factory);
    }
    return instance;
  }
  
  @Override
  public void run() {
    routeGetFromGame();
  }
  
  private void routeGetFromGame() {
    Spark.get(ROUTE_GET_FROM_GAME, "application/json", (req, resp) -> { // :game_id
      EntityManager em = factory.createEntityManager();
      final GameRepository gameRepository = new GameRepository(em);
    
      long gameId;
      try {
        gameId = Long.parseLong(req.params(":game_id"));
      } catch (NumberFormatException e) {
        resp.status(403);
        return ErrorMessages.informationNotNumber("Game ID");
      }
    
      Optional<Game> game = gameRepository.findById(gameId);
    
      if (game.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("Game");
      }
    
      resp.type("application/json");
      resp.status(201);
  
      NewsRepository newsRepository = new NewsRepository(em);
      List<News> news = newsRepository.findByGame(game.get());
  
      JsonArray jsonArray = new JsonArray();
      news.forEach(n -> jsonArray.add(n.asJson()));
      
      em.close();
      return jsonArray;
    });
  }
}
