package controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import values.ErrorMessages;
import values.Token;
import interfaces.Controller;
import model.Game;
import model.News;
import model.User;
import repositories.GameRepository;
import repositories.NewsRepository;
import repositories.UserRepository;
import services.AccessControlService;
import services.NewsService;
import spark.Spark;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;

public class NewsController implements Controller {
  
  private static final String ROUTE_GET_FROM_GAME = "/news/get/game/:game_id";  // nothing
  private static final String ROUTE_GET_FROM_DEV = "/news/get/dev/:user_id/:max";  // nothing
  private static final String ROUTE_ADD_TO_GAME = "/news/add/game/:game_id";    // body: title,description | header: token
  private static final String ROUTE_DELETE_BY_ID = "/news/delete/id/:news_id";  // header: token
  
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
    routeGetFromDev();
    routeAddToGame();
    routeDeleteById();
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
      resp.status(200);
  
      NewsRepository newsRepository = new NewsRepository(em);
      List<News> news = newsRepository.findByGame(game.get());
  
      JsonArray jsonArray = new JsonArray();
      news.forEach(n -> jsonArray.add(n.asJson()));
      
      em.close();
      return jsonArray;
    });
  }

  private void routeGetFromDev() {
    Spark.get(ROUTE_GET_FROM_DEV, "application/json", (req, resp) -> { // :user_id
      EntityManager em = factory.createEntityManager();
      UserRepository userRepository = new UserRepository(em);

      int max;
      try {
        max = Integer.parseInt(req.params(":max"));
      } catch (NumberFormatException e) {
        resp.status(403);
        return ErrorMessages.informationNotNumber("Max");
      }

      long userId;
      try {
        userId = Long.parseLong(req.params(":user_id"));
      } catch (NumberFormatException e) {
        resp.status(403);
        return ErrorMessages.informationNotNumber("User ID");
      }

      Optional<User> user = userRepository.findById(userId);

      if (user.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("User");
      }

      resp.type("application/json");
      resp.status(200);

      NewsRepository newsRepository = new NewsRepository(em);
      List<News> news = newsRepository.findByAuthor(user.get(), max);

      JsonArray jsonArray = new JsonArray();
      news.forEach(n -> jsonArray.add(n.asJson()));

      em.close();
      return jsonArray;
    });
  }

  private void routeAddToGame() {
    Spark.post(ROUTE_ADD_TO_GAME, "application/json", (req, resp) -> {
      // :game_id | body: title,description | header: token
      EntityManager em = factory.createEntityManager();


      String token = req.headers(Token.PROPERTY_NAME);
      if (token == null || !AccessControlService.isTokenValid(token)) {
        resp.status(401);
        return ErrorMessages.userMustBeLoggedIn();
      }

      String username = AccessControlService.getUsernameFromToken(token);
      UserRepository userRepository = new UserRepository(em);
      Optional<User> user = userRepository.findByUsername(username);
      if (user.isEmpty()) {
        resp.status(404);
        return ErrorMessages.usernameNotFound(username);
      }

      String gameId = req.params(":game_id");
      GameRepository gameRepository = new GameRepository(em);
      Optional<Game> game = gameRepository.findById(Long.parseLong(gameId));
      if (game.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("Game");
      }
      
      if (!NewsService.isAbleToAddNews(user.get(), game.get())) {
        resp.status(403);
        return ErrorMessages.userNotAllowedToPerformAction();
      }

      JsonObject body = JsonParser
              .parseString(req.body())
              .getAsJsonObject();

      String title = body.get("title").getAsString();
      if (title == null || title.isEmpty()) {
        resp.status(400);
        return ErrorMessages.informationNotProvided("Title");
      }

      String description = body.get("description").getAsString();
      if (description == null || description.isEmpty()) {
        resp.status(400);
        return ErrorMessages.informationNotProvided("Description");
      }

      resp.status(201);
      resp.type("application/json");

      NewsRepository newsRepository = new NewsRepository(em);
      News news = new News(title, description, game.get(), user.get());
      newsRepository.persist(news);

      NewsService.notifyUsers(news, em);

      em.close();
      return news.asJson();
    });
  }

  private void routeDeleteById() {
    Spark.put(ROUTE_DELETE_BY_ID, "application/json", (req, resp) -> {  // :news_id | header: token
      EntityManager em = factory.createEntityManager();

      String token = req.headers(Token.PROPERTY_NAME);
      if (token == null || !AccessControlService.isTokenValid(token)) {
        resp.status(401);
        return ErrorMessages.userMustBeLoggedIn();
      }

      String username = AccessControlService.getUsernameFromToken(token);
      UserRepository userRepository = new UserRepository(em);
      Optional<User> user = userRepository.findByUsername(username);
      if (user.isEmpty()) {
        resp.status(404);
        return ErrorMessages.usernameNotFound(username);
      }

      long newsId;
      try {
        newsId = Long.parseLong(req.params(":news_id"));
      } catch (NumberFormatException e) {
        resp.status(400);
        return ErrorMessages.informationNotNumber("Game ID");
      }

      NewsRepository newsRepository = new NewsRepository(em);

      Optional<News> news = newsRepository.findById(newsId);
      if (news.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("News");
      }

      if (!NewsService.isAbleToDeleteNews(user.get(), news.get())) {
        resp.status(401);
        return ErrorMessages.userNotAllowedToPerformAction();
      }

      newsRepository.deleteById(newsId);

      em.close();

      resp.status(204);
      return "";
    });
  }
}
