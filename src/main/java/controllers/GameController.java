package controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entities.ErrorMessages;
import entities.Token;
import example.ImageExample;
import interfaces.Controller;
import interfaces.Responses;
import model.Game;
import model.Tag;
import model.User;
import repositories.GameRepository;
import repositories.TagRepository;
import repositories.UserRepository;
import services.AccessControlService;
import services.GameService;
import services.UserService;
import spark.Spark;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameController implements Controller {
  private static final String ROUTE_CREATE_GAME = "/game/create";
  private static final String ROUTE_EDIT_GAME = "/game/edit/:game_id";
  private static final String ROUTE_GET_BY_TAG = "/game/get/tag/:tag_id";
  private static final String ROUTE_IS_SUBSCRIBED = "/game/subs/is/:game_id";         // header: token
  private static final String ROUTE_SUBSCRIBE = "/game/subs/subscribe/:game_id";      // header: token
  private static final String ROUTE_UNSUBSCRIBE = "/game/subs/unsubscribe/:game_id";  // header: token
  
  private EntityManagerFactory factory;
  private static Controller instance;
  
  private GameController(EntityManagerFactory factory) {
    this.factory = factory;
  }
  
  public static Controller getInstance(EntityManagerFactory factory) {
    if (instance == null) {
      instance = new GameController(factory);
    }
    return instance;
  }
  
  @Override
  public void run() {
    setRouteCreateGame();
    setRouteEditGame();
    setRouteGetByTag();
    setRouteIsSubscribed();
    setRouteSubscribe();
    setRouteUnsubscribe();
  }

  private void setRouteCreateGame() {
    Spark.post(ROUTE_CREATE_GAME, "application/json", (req, resp) -> {
      // body: name,description,release_date,cover,background_image,tags(list of IDs) | header: token
      EntityManager em = factory.createEntityManager();

      String token = req.headers(Token.PROPERTY_NAME);
      if (token == null || !AccessControlService.isTokenValid(token)) {
        resp.status(401);
        return ErrorMessages.userMustBeLoggedIn();
      }
      String username = AccessControlService.getUsernameFromToken(token); // already checked with token validation
      Optional<User> dev = new UserRepository(em).findByUsername(username);

      if (dev.isEmpty()) {
        resp.status(404);
        return ErrorMessages.usernameNotFound(username);
      }
      if (!UserService.isAbleToCreateGame(dev.get())) {
        resp.status(401);
        return ErrorMessages.userMustBeDeveloper();
      }

      JsonObject body = JsonParser
              .parseString(req.body())
              .getAsJsonObject();

      // check name
      String name = body.get("name").getAsString();
      Responses titleResponse = Game.isNameValid(name);
      if (titleResponse.hasError()) {
        resp.status(404);
        return titleResponse.getMessage();
      }

      // check description
      String description = body.get("description").getAsString();
      Responses descriptionResponse = Game.isDescriptionValid(description);
      if (descriptionResponse.hasError()) {
        resp.status(404);
        return descriptionResponse.getMessage();
      }

      // check release date
      String releaseDateStr = body.get("release_date").getAsString();
      LocalDate releaseDate;
      try {
        releaseDate = LocalDate.parse(releaseDateStr);
      } catch (Exception e) {
        resp.status(400);
        return ErrorMessages.informationIncorrectFormat("release_date");
      }
      Responses releaseDateResponse = Game.isReleaseDateValid(releaseDate);
      if (releaseDateResponse.hasError()) {
        resp.status(404);
        return releaseDateResponse.getMessage();
      }

      // check cover
      JsonElement coverElem = body.get("cover");
      String cover;
      if (coverElem == null || coverElem.isJsonNull() || coverElem.getAsString().isEmpty()) {
        cover = ImageExample.LAZY_COOL_CAT.image;
      } else {
        cover = coverElem.getAsString();
      }

      // check background image
      JsonElement backgroundImageElem = body.get("background_image");
      String backgroundImage;
      if (backgroundImageElem == null || backgroundImageElem.isJsonNull() || backgroundImageElem.getAsString().isEmpty()) {
        backgroundImage = ImageExample.CAT_WITH_A_PROBLEM.image;
      } else {
        backgroundImage = backgroundImageElem.getAsString();
      }

      // check tags
      JsonElement tagsElem = body.get("tags");
      List<Tag> tags = new ArrayList<>();
      try {
        JsonArray array = tagsElem.getAsJsonArray();
        TagRepository tagRepository = new TagRepository(em);
        array.forEach(t ->
                tags.add(
                        tagRepository.findById(t.getAsLong()).get()
                )
        );
      } catch (Exception ignored) {
      }

      resp.type("application/json");
      resp.status(201);

      GameRepository gameRepository = new GameRepository(em);
      Game game = new Game(
              name,
              dev.get(),
              description,
              releaseDate,
              cover,
              backgroundImage
      );
      gameRepository.persist(game);

      tags.forEach(t -> gameRepository.addTag(dev.get(), game, t));

      em.close();
      return game.asJson();
    });

  }

  private void setRouteEditGame() {
    Spark.put(ROUTE_EDIT_GAME, "application/json", (req, resp) -> {
      // :game_id | body: name,description,release_date,cover,background_image,tags(list of IDs) | header: token
      // elements of body are all optional
      EntityManager em = factory.createEntityManager();

      String token = req.headers(Token.PROPERTY_NAME);
      if (token == null || !AccessControlService.isTokenValid(token)) {
        resp.status(401);
        return ErrorMessages.userMustBeLoggedIn();
      }
      String username = AccessControlService.getUsernameFromToken(token);
      Optional<User> dev = new UserRepository(em).findByUsername(username);

      if (dev.isEmpty()) {
        resp.status(404);
        return ErrorMessages.usernameNotFound(username);
      }

      long gameId;
      try {
        gameId = Long.parseLong(req.params(":game_id"));
      } catch (NumberFormatException e) {
        resp.status(403);
        return ErrorMessages.informationNotNumber("Game ID");
      }
      GameRepository gameRepository = new GameRepository(em);
      Optional<Game> game = gameRepository.findById(gameId);

      if (game.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("Game");
      }

      if (!GameService.isAbleToEditGame(dev.get(), game.get())) {
        resp.status(401);
        return ErrorMessages.userMustBeDeveloper();
      }

      JsonObject body = JsonParser
              .parseString(req.body())
              .getAsJsonObject();

      // check name
      try {
        String name = body.get("name").getAsString();
        Responses titleResponse = Game.isNameValid(name);
        if (titleResponse.hasError()) {
          resp.status(404);
          return titleResponse.getMessage();
        }
        game.get().setName(name);
      } catch (Exception ignored) {}

      // check description
      try {
        String description = body.get("description").getAsString();
        Responses descriptionResponse = Game.isDescriptionValid(description);
        if (descriptionResponse.hasError()) {
          resp.status(404);
          return descriptionResponse.getMessage();
        }
        game.get().setDescription(description);
      } catch (Exception ignored) {}

      // check release date
      try {
        String releaseDateStr = body.get("release_date").getAsString();
        LocalDate releaseDate;
        try {
          releaseDate = LocalDate.parse(releaseDateStr);
        } catch (DateTimeParseException e) {
          resp.status(400);
          return ErrorMessages.informationIncorrectFormat("release_date");
        }
        Responses releaseDateResponse = Game.isReleaseDateValid(releaseDate);
        if (releaseDateResponse.hasError()) {
          resp.status(404);
          return releaseDateResponse.getMessage();
        }
        game.get().setReleaseDate(releaseDate);
      } catch (Exception ignored) {}

      // check cover
      try {
        String cover = body.get("cover").getAsString();
        game.get().setCover(cover);
      } catch (Exception ignored) {}

      // check background image
      String backgroundImage;
      try {
        backgroundImage = body.get("background_image").getAsString();
        game.get().setBackgroundImage(backgroundImage);
      } catch (Exception ignored) {}

      // check tags
      try {
        JsonElement tagsElem = body.get("tags");
        List<Tag> tags = new ArrayList<>();
        JsonArray array = tagsElem.getAsJsonArray();
        TagRepository tagRepository = new TagRepository(em);
        array.forEach(t ->
                tags.add(
                        tagRepository.findById(t.getAsLong()).get()
                )
        );
        game.get().setTag(tags);
      } catch (Exception ignored) {}

      resp.type("application/json");
      resp.status(201);

      gameRepository.persist(game.get());

      JsonObject jsonObject = game.get().asJson();
      em.close();
      return jsonObject;
    });

  }

  private void setRouteGetByTag() {
    Spark.get(ROUTE_GET_BY_TAG, "application/json", (req, resp) -> {
      EntityManager em = factory.createEntityManager();
      final TagRepository tagRepository = new TagRepository(em);
    
      long id;
      try {
        id = Long.parseLong(req.params(":tag_id"));
      } catch (NumberFormatException e) {
        resp.status(403);
        return ErrorMessages.informationNotNumber("Tag ID");
      }
    
      Optional<Tag> tag = tagRepository.findById(id);
    
      if (tag.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("Tag");
      }
    
      resp.type("application/json");
      resp.status(201);
  
      List<Game> games = new ArrayList<>(tag.get().getTaggedGames());
  
      JsonArray jsonArray = new JsonArray();
      games.forEach(g -> jsonArray.add(g.asJson()));
      em.close();
    
      return jsonArray;
    });
  
  }

  private void setRouteIsSubscribed() {
    Spark.get(ROUTE_IS_SUBSCRIBED, "application/json", (req, resp) -> {
      EntityManager em = factory.createEntityManager();

      String token = req.headers(Token.PROPERTY_NAME);
      if (token == null || !AccessControlService.isTokenValid(token)) {
        resp.status(401);
        return ErrorMessages.userMustBeLoggedIn();
      }
      String username = AccessControlService.getUsernameFromToken(token);
      Optional<User> user = new UserRepository(em).findByUsername(username);

      if (user.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("User");
      }

      long gameId;
      try {
        gameId = Long.parseLong(req.params(":game_id"));
      } catch (NumberFormatException e) {
        resp.status(403);
        return ErrorMessages.informationNotNumber("Game ID");
      }

      GameRepository gameRepository = new GameRepository(em);
      Optional<Game> game = gameRepository.findById(gameId);

      if (game.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("Game");
      }

      resp.type("application/json");
      resp.status(200);

      boolean isSubscribed = game.get().getSubscribers().contains(user.get());

      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("is_subscribed", isSubscribed);

      em.close();
      return jsonObject;
    });
  }

  private void setRouteSubscribe() {
    Spark.post(ROUTE_SUBSCRIBE, "application/json", (req, resp) -> {
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
        return ErrorMessages.informationNotFound("User");
      }

      long gameId;
      try {
        gameId = Long.parseLong(req.params(":game_id"));
      } catch (NumberFormatException e) {
        resp.status(403);
        return ErrorMessages.informationNotNumber("Game ID");
      }

      GameRepository gameRepository = new GameRepository(em);
      Optional<Game> game = gameRepository.findById(gameId);

      if (game.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("Game");
      }

      resp.type("application/json");
      resp.status(204);

      userRepository.subscribe(user.get(), game.get());

      em.close();
      return "";
    });
  }

  private void setRouteUnsubscribe() {
    Spark.post(ROUTE_UNSUBSCRIBE, "application/json", (req, resp) -> {
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
        return ErrorMessages.informationNotFound("User");
      }

      long gameId;
      try {
        gameId = Long.parseLong(req.params(":game_id"));
      } catch (NumberFormatException e) {
        resp.status(403);
        return ErrorMessages.informationNotNumber("Game ID");
      }

      GameRepository gameRepository = new GameRepository(em);
      Optional<Game> game = gameRepository.findById(gameId);

      if (game.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("Game");
      }

      resp.type("application/json");
      resp.status(204);

      userRepository.unsubscribe(user.get(), game.get());

      em.close();
      return "";
    });
  }
}
