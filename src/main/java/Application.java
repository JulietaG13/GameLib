import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entities.Rol;
import entities.Token;
import entities.responses.UserResponse;
import example.BDExample;
import interfaces.Controller;
import interfaces.Responses;
import model.*;
import persistence.Database;
import repositories.*;
import services.*;
import controllers.*;
import spark.Spark;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class Application {
  
  private static final Gson gson = new Gson();
  private static User admin;

  private static EntityManagerFactory factory;

  static {
    admin = User.create("admin")
            .email("admin@admin.admin")
            .password("admin")
            .rol(Rol.ADMIN)
            .build();
  }
  
  public static void main(String[] args) {
    new Database().startDBServer();
    factory = Persistence.createEntityManagerFactory("gamelib");
    
    Spark.port(4567);

    storeAdmin(admin, getEntityManager());

    EntityManager entityManager = getEntityManager();
    storeUsers1(entityManager);
    storeGames1(entityManager);
    storeTags1(entityManager);
    storeReviews1(entityManager);
    new BDExample(entityManager).store();
    entityManager.close();

    controllers(factory);

    Spark.get("/users", "application/json", (req, resp) -> {
      
      resp.type("application/json");
      resp.status(201);

      EntityManager em = getEntityManager();
      UserRepository userRepository = new UserRepository(em);

      JsonArray jsonArray = new JsonArray();
      for (User user : userRepository.listAll()) {
        jsonArray.add(user.asJson());
      }

      em.close();
      return jsonArray.toString();
    });

    Spark.get("/games", "application/json", (req, resp) -> {

      resp.type("application/json");
      resp.status(201);

      EntityManager em = getEntityManager();
      GameRepository gameRepository = new GameRepository(em);

      JsonArray jsonArray = new JsonArray();
      for (Game game : gameRepository.listAll()) {
        jsonArray.add(game.asJson());
      }

      em.close();
      return jsonArray.toString();
    });
    
    Spark.get("/tags", "application/json", (req, resp) -> {
      
      resp.type("application/json");
      resp.status(201);

      EntityManager em = getEntityManager();
      TagRepository tagRepository = new TagRepository(em);
      JsonArray jsonArray = new JsonArray();
      for (Tag tag : tagRepository.listAll()) {
        jsonArray.add(tag.asJson());
      }

      em.close();
      return jsonArray;
    });
    
    Spark.post("/tokenvalidation", "application/json", (req, resp) -> {
      String token = req.headers(Token.PROPERTY_NAME);
      if (token == null || !AccessControlService.isTokenValid(token)) {
        resp.status(401);
        return "Token is invalid or has expired!";
      }
      
      resp.type("application/json");
      resp.status(201);

      EntityManager em = getEntityManager();
      UserRepository userRepository = new UserRepository(em);
      String username = AccessControlService.getUsernameFromToken(token);
      Optional<User> user = userRepository.findByUsername(username);
      em.close();
      
      if (user.isEmpty()) {
        resp.status(404);
        return "User not found!";
      }

      return user.get().asJson();
    });
    
    Spark.post("/newuser", "application/json", (req, resp) -> {
      final User user = User.fromJson(req.body());
      
      Responses response = AccessControlService.isFormatValid(user);
      if (response.hasError()) {
        resp.status(response.getStatusCode());
        return response.getMessage();
      }
      
      if (user.getRol() == null) {    // default
        user.setRol(Rol.USER);
      }

      EntityManager em = getEntityManager();
      response = AccessControlService.isUserAvailable(user, em);
      if (response.hasError()) {
        resp.status(response.getStatusCode());
        return response.getMessage();
      }
      
      final UserRepository userRepository = new UserRepository(em);
      userRepository.persist(user);
      
      resp.type("application/json");
      resp.status(201);

      em.close();
      return user.asJson().toString();
    });

    Spark.get("/getuser/:username", "application/json", (req, resp) -> {
      String username = req.params(":username");
      if (username == null || username.isEmpty()) {
        resp.status(404);
        return "Username not found!";
      }

      EntityManager em = getEntityManager();
      UserRepository userRepository = new UserRepository(em);
      Optional<User> user = userRepository.findByUsername(username);

      if (user.isEmpty()) {
        resp.status(404);
        return "There is no User " + username +"!";
      }

      resp.type("application/json");
      resp.status(200);

      em.close();
      return user.get().asJson();
    });

    Spark.get("/getprofile/:username", "application/json", (req, resp) -> {
      String username = req.params(":username");
      if (username == null || username.isEmpty()) {
        resp.status(404);
        return "Username not found!";
      }

      EntityManager em = getEntityManager();
      UserRepository userRepository = new UserRepository(em);
      Optional<User> user = userRepository.findByUsername(username);

      if (user.isEmpty()) {
        resp.status(404);
        return "There is no User " + username +"!";
      }

      resp.type("application/json");
      resp.status(200);

      em.close();
      return user.get().asJsonProfile();
    });

    Spark.post("/deleteuser/:username", "application/json", (req, resp) -> {
      String token = req.headers(Token.PROPERTY_NAME);
      if (token == null || !AccessControlService.isTokenValid(token)) {
        resp.status(401);
        return "Token is invalid or has expired!";
      }

      /*
      long id;
      try {
        id = Long.parseLong(req.params("id"));
      } catch (NumberFormatException e) {
        resp.status(403);
        return "User ID must be a number!";
      }
      */
      String username = req.params("username");

      EntityManager em = getEntityManager();
      UserRepository userRepository = new UserRepository(em);
      Optional<User> user = userRepository.findByUsername(username);
      if (user.isEmpty()) {
        resp.status(404);
        return "Theres no user with username " + username + "!";
      }

      userRepository.deleteUserByID(user.get().getId());

      resp.type("application/json");
      resp.status(200);

      em.close();
      return user.get().asJson().toString();
    });
    
    Spark.post("/login", "application/json", (req, resp) -> {
      final User user = User.fromJson(req.body());
      final String username = user.getUsername();
      final String password = user.getPassword();

      Responses usernameResponse = AccessControlService.isUsernameValid(username);
      if (usernameResponse.hasError()) {
        resp.status(usernameResponse.getStatusCode());
        return UserResponse.genericMessage();
      }

      Responses passwordResponse = AccessControlService.isPasswordValid(password);
      if (passwordResponse.hasError()) {
        resp.status(passwordResponse.getStatusCode());
        return UserResponse.genericMessage();
      }

      EntityManager em = getEntityManager();
      Responses authResponse = AccessControlService.authenticateUser(username, password, em);
      em.close();
      if (authResponse.hasError()) {
        resp.status(authResponse.getStatusCode());
        return UserResponse.genericMessage();
      }
      resp.status(200);
      
      Token token = AccessControlService.generateToken(username);
      
      JsonObject json = authResponse.getUser().asJson();
      
      json.addProperty(Token.PROPERTY_NAME, token.getToken());
      
      resp.type("application/json");
      return json;
    });
    
    Spark.post("/newgame", "application/json", (req, resp) -> {
      String token = req.headers(Token.PROPERTY_NAME);
      if (token == null || !AccessControlService.isTokenValid(token)) {
        resp.status(401);
        return "Token is invalid or has expired!";
      }
      String username = AccessControlService.getUsernameFromToken(token); // already checked with token validation
      EntityManager em1 = getEntityManager();
      Optional<User> dev = new UserRepository(em1).findByUsername(username);
      em1.close();

      if (dev.isEmpty()) {
        resp.status(404);
        return "There is no user with username " + username + "!";
      }

      EntityManager em2 = getEntityManager();
      final GameRepository games = new GameRepository(em2);
      if (!games.isUserAllowed(dev.get())) {
        resp.status(403);
        return "User is not allowed!";
      }

      final Game game = Game.fromJson(req.body());

      /*
      Responses pictureResponse = Game.isGamePictureValid(game.getGamePicture());
      if (pictureResponse.hasError()) {
        resp.status(404);
        return pictureResponse.getMessage();
      }
      */
      
      Responses titleResponse = Game.isNameValid(game.getName());
      if (titleResponse.hasError()) {
        resp.status(404);
        return titleResponse.getMessage();
      }
      
      Responses descriptionResponse = Game.isDescriptionValid(game.getDescription());
      if (descriptionResponse.hasError()) {
        resp.status(404);
        return descriptionResponse.getMessage();
      }

      Responses releaseDateResponse = Game.isReleaseDateValid(game.getReleaseDate());
      if (releaseDateResponse.hasError()) {
        resp.status(404);
        return releaseDateResponse.getMessage();
      } else {
        game.setLastUpdate(game.getReleaseDate());
      }
      
      games.persist(game);
      resp.type("application/json");
      resp.status(201);

      em2.close();
      return game.asJson();
    });
    
    Spark.get("/getgame/:id", "application/json", (req, resp) -> {
      EntityManager em1 = getEntityManager();
      final GameRepository gameRepository = new GameRepository(em1);
      
      long id;
      try {
        id = Long.parseLong(req.params(":id"));
      } catch (NumberFormatException e) {
        resp.status(403);
        return "Game ID must be a number!";
      }
      
      Optional<Game> game = gameRepository.findById(id);
      
      if (game.isEmpty()) {
        resp.status(404);
        return "There's no game with id " + req.params(":id") + "!";
      }
      
      resp.type("application/json");
      resp.status(201);

      JsonObject jsonObject = game.get().asJson();
      em1.close();

      return jsonObject.toString();
    });
    
    Spark.put("/editgame/:id", "application/json", (req, resp) -> {
      String token = req.headers(Token.PROPERTY_NAME);
      if (token == null || !AccessControlService.isTokenValid(token)) {
        resp.status(401);
        return "Token is invalid or has expired!";
      }
      String username = AccessControlService.getUsernameFromToken(token); // already checked with token validation
      EntityManager em1 = getEntityManager();
      Optional<User> owner = new UserRepository(em1).findByUsername(username);
      em1.close();

      if (owner.isEmpty()) {
        resp.status(404);
        return "There is no user with username " + username + "!";
      }

      EntityManager em2 = getEntityManager();
      final GameRepository gameRepository = new GameRepository(em2);
      long id;
      try {
        id = Long.parseLong(req.params(":id"));
      } catch (NumberFormatException e) {
        resp.status(403);
        return "Game ID must be a number!";
      }
      
      Optional<Game> gameToEdit = gameRepository.findById(id);
      
      if (gameToEdit.isEmpty()) {
        resp.status(404);
        return "There's no game with id " + req.params(":id") + "!";
      }
      
      Game gameUpdate = Game.fromJson(req.body());
      //LocalDateTime lastUpdate = LocalDateTime.now();
      LocalDateTime lastUpdate = gameUpdate.getLastUpdate(); //TODO(have front send LocalDateTime)
      
      Responses gameResponse = gameRepository.update(owner.get(), id, gameUpdate, lastUpdate);
      em2.close();
      if (gameResponse.hasError()) {
        resp.status(gameResponse.getStatusCode());
        return gameResponse.getMessage();
      }
      
      resp.type("application/json");
      resp.status(201);
      
      return gameResponse.getGame().asJson();
    });
    
    Spark.delete("/deletegame/:id", "application/json", (req, res) -> {
      //TODO(get token from header and validate it)
      String token = req.headers(Token.PROPERTY_NAME);
      if (token == null || !AccessControlService.isTokenValid(token)) {
        res.status(401);
        return "Token is invalid or has expired!";
      }

      //System.out.println("Esta línea es absolutamente necesaria para que la eliminacion funcione correctamente");
      EntityManager em = getEntityManager();
      final GameRepository gameRepository = new GameRepository(em);
      
      long id;
      try {
        id = Long.parseLong(req.params(":id"));
      } catch (NumberFormatException e) {
        res.status(403);
        return "Game ID must be a number!";
      }
      
      Optional<Game> game = gameRepository.findById(id);

      if (game.isEmpty()) {
        res.status(404);
        return "There's no game with id " + req.params(":id") + "!";
      }

      gameRepository.delete(id);
      em.close();

      res.type("application/json");
      res.status(200);
      return game.get().asJson();
    });
    
    Spark.get("/latestupdated/:max", "application/json", (req, resp) -> {
      EntityManager em = getEntityManager();
      final GameRepository gameRepository = new GameRepository(em);
      
      int max;
      try {
        max = Integer.parseInt(req.params("max"));
      } catch (NumberFormatException e) {
        resp.status(403);
        return "Max number of games must be a number!";
      }
      
      List<Game> latestUpdated = gameRepository.listByLatest(max);
      em.close();
      resp.type("application/json");
      resp.status(201);
      
      JsonObject jsonObj = new JsonObject();
      jsonObj.addProperty("actual", latestUpdated.size());
      
      JsonArray jsonArray = new JsonArray(latestUpdated.size());
      for(Game game : latestUpdated) {
        JsonObject jsonGame = game.asJson();
        jsonArray.add(jsonGame);
      }
      
      jsonObj.add("games", jsonArray);
      
      return jsonObj.toString();
    });
    
    Spark.post("/newreview/:game_id", "application/json", ((req, resp) -> {
      String token = req.headers(Token.PROPERTY_NAME);
      if (token == null || !AccessControlService.isTokenValid(token)) {
        resp.status(401);
        return "Token is invalid or has expired!";
      }
      
      Long gameId = Long.parseLong(req.params("game_id"));

      EntityManager em1 = getEntityManager();
      EntityManager em2 = getEntityManager();
      UserRepository userRepository = new UserRepository(em1);
      GameRepository gameRepository = new GameRepository(em2);
      String username = AccessControlService.getUsernameFromToken(token);
      Optional<User> user = userRepository.findByUsername(username);
      Optional<Game> game = gameRepository.findById(gameId);
      em1.close();
      em2.close();
      
      if (user.isEmpty()) {   // should not happen but who knows
        resp.status(404);
        return "User with username " + username + " does not exist!";
      }
      
      if (game.isEmpty()) {   // should not happen but who knows
        resp.status(404);
        return "There is no game with id " + gameId + "!";
      }

      EntityManager em3 = getEntityManager();
      ReviewRepository reviewRepository = new ReviewRepository(em3);
      Review review = Review.fromJson(req.body());
      review.setAuthor(user.get());
      review.setGame(game.get());
      
      reviewRepository.persist(review);
      em3.close();
      
      resp.type("application/json");
      resp.status(201);
      return review.asJson();
    }));
  
    Spark.get("/getreviews/:game_id/:max", "application/json", (req, resp) -> {
  
      long gameId;
      try {
        gameId = Integer.parseInt(req.params(":game_id"));
      } catch (NumberFormatException e) {
        resp.status(403);
        return "game_id must be a number!";
      }
      
      int max;
      try {
        max = Integer.parseInt(req.params(":max"));
      } catch (NumberFormatException e) {
        resp.status(403);
        return "Max number of reviews must be a number!";
      }

      EntityManager em = getEntityManager();
      ReviewRepository reviewRepository = new ReviewRepository(em);
      GameRepository gameRepository = new GameRepository(em);
      Optional<Game> game = gameRepository.findById(gameId);
  
      if (game.isEmpty()) {
        resp.status(404);
        return "There is no game with id " + gameId + "!";
      }
      
      List<Review> reviews = reviewRepository.listByGame(game.get());
      
      JsonObject jsonObj = new JsonObject();
      jsonObj.addProperty("actual", reviews.size());
    
      JsonArray jsonArray = new JsonArray();
      reviews.forEach(r -> jsonArray.add(r.asJson()));
    
      jsonObj.add("reviews", jsonArray);
      
      resp.type("application/json");
      resp.status(201);

      em.close();
      return jsonArray.toString();
    });
    
    Spark.options("/*", (req, res) -> {
      String accessControlRequestHeaders = req.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        res.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }
      
      String accessControlRequestMethod = req.headers("Access-Control-Request-Method");
      if (accessControlRequestMethod != null) {
        res.header("Access-Control-Allow-Methods", accessControlRequestMethod);
      }
      
      return "OK";
    });
    
    Spark.before((req, res) -> {
      res.header("Access-Control-Allow-Origin", "*");
      res.header("Access-Control-Allow-Headers", "*");
      res.type("application/json");
    });

    Spark.exception(Exception.class, (e, request, response) -> {
      final StringWriter sw = new StringWriter();
      final PrintWriter pw = new PrintWriter(sw, true);
      e.printStackTrace(pw);
      System.err.println(sw.getBuffer().toString());
    });
  }

  private static void storeAdmin(User admin, EntityManager entityManager) {
    UserRepository userRepository = new UserRepository(entityManager);
    userRepository.persist(admin);
  }
  
  // tests for BD //
  
  private static void storeUsers1(EntityManager entityManager) {
    UserRepository userRepository = new UserRepository(entityManager);
    
    if(userRepository.listAll().size() < 4) {
      for(int i = 1; i < 5; i++) {
        User u = User.create("username" + i).email("user" + i + "@mail.com").password("qwerty123").build();
        userRepository.persist(u);
      }
    }
  }
  
  private static void storeGames1(EntityManager entityManager) {
    GameRepository gameRepository = new GameRepository(entityManager);
    ShelfRepository shelfRepository = new ShelfRepository(entityManager);
    UserRepository userRepository = new UserRepository(entityManager);

    User user = User.create("IOwnAShelf").email("shelves@mail.com").password("1234").build();
    User developer = User.create("IOwnGames").email("games@mail.com").password("1234").rol(Rol.DEVELOPER).build();
    Shelf shelf = new Shelf(user, "elf on a shelf");
    Game game1 = Game
        .create("awesome game")
        .description("just an awesome game")
        .releaseDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
        .lastUpdate(LocalDateTime.now())
        .build();
    Game game2 = Game
        .create("another awesome game")
        .description("just another awesome game")
        .releaseDate(LocalDateTime.now().plusMonths(4).truncatedTo(ChronoUnit.SECONDS))
        .lastUpdate(LocalDateTime.now().plusMonths(4))
        .build();
    Game game3 = Game
        .create("even another awesome game")
        .description("also an awesome game")
        .releaseDate(LocalDateTime.now().plusMonths(2).truncatedTo(ChronoUnit.SECONDS))
        .lastUpdate(LocalDateTime.now().plusMonths(2))
        .build();
    
    if (gameRepository.listAll().isEmpty() && shelfRepository.listAll().isEmpty()) {
      userRepository.persist(user);
      userRepository.persist(developer);
      gameRepository.persist(game1);
      gameRepository.persist(game2);
      gameRepository.persist(game3);
      shelfRepository.persist(shelf);
    }

    developer.addDeveloped(game1);
    developer.addDeveloped(game2);
    
    shelfRepository.addGame(shelf, user, game1);
    shelfRepository.addGame(shelf, user, game3);
    
    News news1 = new News("first news", "this is this game's first news!!!", game1, developer);
    News news2 = new News("second news", "another news", game1, developer);
    NewsRepository newsRepository = new NewsRepository(entityManager);
    newsRepository.persist(news1);
    newsRepository.persist(news2);
  }
  
  private static void storeTags1(EntityManager entityManager) {
    TagRepository tagRepository = new TagRepository(entityManager);
    GameRepository gameRepository = new GameRepository(entityManager);

    String cover = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAkACQAAD/2wBDAAIBAQIBAQICAgICAgICAwUDAwMDAwYEBAMFBwYHBwcGBwcICQsJCAgKCAcHCg0KCgsMDAwMBwkODw0MDgsMDAz/2wBDAQICAgMDAwYDAwYMCAcIDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAz/wAARCAAKAAoDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9BbX/AILufBvVz8d9WtPjx+zzH4Z8D2cUHhIXep39vqc+orBMLkXlu8Qe5t/tAgET6cs5dDJ/FtByvhb/AMHN/wCx/dfDLw5J4w+OnhWHxdJpds2tpo/hzX205L4xKbgWxmsllMIl37DIqvt27gDkV/OF/wAF2PCel+B/+CvXx90vRdM0/R9MtvFMphtLG3S3gi3Rxu21EAUZZmY4HJYnqa/qH/ZI/wCCXH7MviT9lL4Y6jqP7OnwJ1DUNQ8J6Vc3V1c+AdKlmuZXs4meR3aAlmZiSWJJJJJoA//Z";
    
    if(tagRepository.listAll().isEmpty()) {
      for(int i = 1; i < 5; i++) {
        Tag t = new Tag("tag" + i);
        tagRepository.persist(t);
      }
    }
    
    List<Tag> tags = tagRepository.listAll();
    
    Game game1 = Game
        .create("tagged game 1")
        .cover(cover)
        .description("a game with 1 tag")
        .releaseDate(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS))
        .lastUpdate(LocalDateTime.now().plusDays(2))
        .build();
    Game game2 = Game
        .create("tagged game 2")
        .cover(cover)
        .description("a game with 3 tags")
        .releaseDate(LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.SECONDS))
        .lastUpdate(LocalDateTime.now().plusDays(3))
        .build();
    gameRepository.persist(game1);
    gameRepository.persist(game2);
    
    gameRepository.addTag(admin, game1, tags.get(0));
    
    gameRepository.addTag(admin, game2, tags.get(2));
    gameRepository.addTag(admin, game2, tags.get(3));
  }
  
  private static void storeReviews1(EntityManager entityManager) {
    ReviewRepository reviewRepository = new ReviewRepository(entityManager);
    UserRepository userRepository = new UserRepository(entityManager);
    GameRepository gameRepository = new GameRepository(entityManager);

    String cover = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAkACQAAD/2wBDAAIBAQIBAQICAgICAgICAwUDAwMDAwYEBAMFBwYHBwcGBwcICQsJCAgKCAcHCg0KCgsMDAwMBwkODw0MDgsMDAz/2wBDAQICAgMDAwYDAwYMCAcIDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAz/wAARCAAKAAoDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9BbX/AILufBvVz8d9WtPjx+zzH4Z8D2cUHhIXep39vqc+orBMLkXlu8Qe5t/tAgET6cs5dDJ/FtByvhb/AMHN/wCx/dfDLw5J4w+OnhWHxdJpds2tpo/hzX205L4xKbgWxmsllMIl37DIqvt27gDkV/OF/wAF2PCel+B/+CvXx90vRdM0/R9MtvFMphtLG3S3gi3Rxu21EAUZZmY4HJYnqa/qH/ZI/wCCXH7MviT9lL4Y6jqP7OnwJ1DUNQ8J6Vc3V1c+AdKlmuZXs4meR3aAlmZiSWJJJJJoA//Z";
    
    if (reviewRepository.listAll().isEmpty()) {
      User u1 = User.create("i made 1 review").email("review1@mail.com").password("qwerty123").build();
      User u2 = User.create("i made 3 reviews").email("review3@mail.com").password("qwerty123").build();
      userRepository.persist(u1);
      userRepository.persist(u2);
      
      Game g1 = Game.create("game with 2 user reviews").cover(cover).description("the game has 2 reviews each by different users").releaseDate(LocalDateTime.now().plusDays(11).truncatedTo(ChronoUnit.SECONDS)).build();
      Game g2 = Game.create("game reviewd by same user").cover(cover).description("the game has 2 reviews by the same user").releaseDate(LocalDateTime.now().plusDays(13).truncatedTo(ChronoUnit.SECONDS)).build();
      gameRepository.persist(g1);
      gameRepository.persist(g2);
      
      Review r1 = new Review("this game is subpar if i say so myself");
      Review r2 = new Review("awesome game 10/10 would recommend i love it");
      reviewRepository.addReview(r1, u1, g1);
      reviewRepository.addReview(r2, u2, g1);
      
      Review r3 = new Review("a review");
      Review r4 = new Review("a second review");
      reviewRepository.addReview(r3, u2, g2);
      reviewRepository.addReview(r4, u2, g2);
      
      reviewRepository.likeReview(r1, u1);   // se auto-likeo
      reviewRepository.dislikeReview(r1, u2);
      
      reviewRepository.dislikeReview(r2, u1);
      
      reviewRepository.likeReview(r3, u1);
      reviewRepository.likeReview(r4, u1);
    }
  }

  private static EntityManager getEntityManager() {
    return factory.createEntityManager();
  }

  private static void controllers(EntityManagerFactory factory) {
    List<Controller> controllers = List.of(
        UserController.getInstance(factory),
        GameController.getInstance(factory),
        ShelfController.getInstance(factory),
        TagController.getInstance(factory),
        NewsController.getInstance(factory)
    );
    controllers.forEach(Controller::run);
  }
}

