import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entities.responses.GameResponse;
import entities.responses.MessageResponse;
import entities.responses.StatusResponse;
import entities.Token;
import entities.responses.UserResponse;
import model.*;
import persistence.Database;
import services.*;
import spark.Spark;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class Application {

    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        new Database().startDBServer();
        final EntityManagerFactory factory = Persistence.createEntityManagerFactory("gamelib");
        final EntityManager em = factory.createEntityManager(); // one for all

        Spark.port(4567);

        storeUsers1(em);
        storeGames1(em);
        storeTags1(em);
        storeReviews1(em);

        Spark.get("/users", "application/json", (req, resp) -> {

            resp.type("application/json");
            resp.status(201);

            UserService userService = new UserService(em);

            return gson.toJson(userService.listAll());
        });
    
        Spark.get("/tags", "application/json", (req, resp) -> {
        
            resp.type("application/json");
            resp.status(201);
        
            TagService tagService = new TagService(em);
            JsonArray jsonArray = new JsonArray();
            for (Tag tag : tagService.listAll()) {
                jsonArray.add(tag.asJson());
            }
        
            return jsonArray;
        });
    
        Spark.post("/tokenvalidation", "application/json", (req, resp) -> {
            /*
            String token = JsonParser.parseString(req.body())
                .getAsJsonObject()
                .get(Token.PROPERTY_NAME)
                .getAsString();
            */
            String token = req.headers(Token.PROPERTY_NAME);
            if (token == null || !AccessControlService.isTokenValid(token)) {
                resp.status(401);
                return "Token is invalid or has expired!";
            }
        
            resp.type("application/json");
            resp.status(201);
        
            UserService userService = new UserService(em);
            String username = AccessControlService.getUsernameFromToken(token);
            Optional<User> user = userService.findByUsername(username);
            
            if (user.isEmpty()) {
                resp.status(404);
                return "User not found!";
            }
            
            return user.get().asJson();
        });

        Spark.post("/newuser", "application/json", (req, resp) -> {
            final User user = User.fromJson(req.body());

            StatusResponse response = AccessControlService.isFormatValid(user);
            if(response.hasError()) {
                resp.status(response.statusCode);
                return response.getMessage();
            }

            if (user.getRol() == null) {    // default
                user.setRol(Rol.USER);
            }

            response = AccessControlService.isUserAvailable(user, em);
            if(response.hasError()) {
                resp.status(response.statusCode);
                return response.getMessage();
            }

            final UserService userService = new UserService(em);
            userService.persist(user);

            resp.type("application/json");
            resp.status(201);

            return user.asJson();
        });

        Spark.post("/login", "application/json", (req, resp) -> {
            final User user = User.fromJson(req.body());
            final String username = user.getUsername();
            final String password = user.getPassword();

            StatusResponse usernameResponse = AccessControlService.isUsernameValid(username);
            if(usernameResponse.hasError()) {
                resp.status(usernameResponse.statusCode);
                return usernameResponse.getMessage();
            }

            StatusResponse passwordResponse = AccessControlService.isPasswordValid(password);
            if(passwordResponse.hasError()) {
                resp.status(passwordResponse.statusCode);
                return passwordResponse.getMessage();
            }

            UserResponse authResponse = AccessControlService.authenticateUser(username, password, em);
            if(authResponse.hasError()) {
                resp.status(authResponse.statusCode);
                return authResponse.getMessage();
            }
            resp.status(200);

            Token token = AccessControlService.generateToken(username);

            JsonObject json = JsonParser
                .parseString(authResponse.getUser().asJson())
                .getAsJsonObject();

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
            
            final Game game = Game.fromJson(req.body());
            final GameService games = new GameService(em);

            MessageResponse titleResponse = Game.isNameValid(game.getName());
            if (titleResponse.hasError()) {
                resp.status(404);
                return titleResponse.getMessage();
            }

            MessageResponse descriptionResponse = Game.isDescriptionValid(game.getDescription());
            if (descriptionResponse.hasError()) {
                resp.status(404);
                return descriptionResponse.getMessage();
            }

            //TODO(just to test for now)
            game.setReleaseDate(LocalDateTime.now(), LocalDateTime.now());

            games.persist(game);
            resp.type("application/json");
            resp.status(201);

            return game.asJson();
        });

        Spark.get("/getgame/:id", "application/json", (req, resp) -> {
            final GameService gameService = new GameService(em);

            long id;
            try {
                id = Long.parseLong(req.params(":id"));
            } catch (NumberFormatException e) {
                resp.status(403);
                return "Game ID must be a number!";
            }

            Optional<Game> game = gameService.findById(id);

            if (game.isEmpty()) {
                resp.status(404);
                return "There's no game with id " + req.params(":id") + "!";
            }

            resp.type("application/json");
            resp.status(201);

            return game.get().asJson();
        });

        Spark.put("/editgame/:id", "application/json", (req, resp) -> {
            String token = req.headers(Token.PROPERTY_NAME);
            if (token == null || !AccessControlService.isTokenValid(token)) {
                resp.status(401);
                return "Token is invalid or has expired!";
            }
            
            final GameService gameService = new GameService(em);
            long id;
            try {
                id = Long.parseLong(req.params(":id"));
            } catch (NumberFormatException e) {
                resp.status(403);
                return "Game ID must be a number!";
            }

            Optional<Game> gameToEdit = gameService.findById(id);

            if (gameToEdit.isEmpty()) {
                resp.status(404);
                return "There's no game with id " + req.params(":id") + "!";
            }

            Game gameUpdate = Game.fromJson(req.body());
            //LocalDateTime lastUpdate = LocalDateTime.now();
            LocalDateTime lastUpdate = gameUpdate.getLastUpdate(); //TODO(have front send LocalDateTime)

            GameResponse gameResponse = gameService.update(id, gameUpdate, lastUpdate);
            if (gameResponse.hasError()) {
                resp.status(404);
                return gameResponse.getMessage();
            }

            resp.type("application/json");
            resp.status(201);

            return gameResponse.getGame().asJson();
        });

        Spark.delete("/deletegame/:id", "application/json", (req, res) -> {
            //TODO(get token from header and validate it)
            System.out.println(2);
            //System.out.println("Esta línea es absolutamente necesaria para que la eliminacion funcione correctamente");
            final GameService gameService = new GameService(em);

            System.out.println(3);

            long id;
            try {
                id = Long.parseLong(req.params(":id"));
            } catch (NumberFormatException e) {
                res.status(403);
                return "Game ID must be a number!";
            }

            Optional<Game> game = gameService.findById(id);

            if (game.isEmpty()) {
                res.status(404);
                return "There's no game with id " + req.params(":id") + "!";
            }

            System.out.println(4);

            gameService.delete(id);

            res.status(200);
            return "Game with id " + req.params(":id") + " has been deleted!";
        });

        Spark.get("/latestupdated/:max", "application/json", (req, resp) -> {
            final GameService gameService = new GameService(em);

            int max;
            try {
                max = Integer.parseInt(req.params("max"));
            } catch (NumberFormatException e) {
                resp.status(403);
                return "Max number of games must be a number!";
            }

            List<Game> latestUpdated = gameService.listByLatest(max);
            resp.type("application/json");
            resp.status(201);

            JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("actual", latestUpdated.size());

            JsonArray jsonArray = new JsonArray(latestUpdated.size());
            for(Game game : latestUpdated) {
                JsonObject jsonGame = JsonParser
                    .parseString(game.asJson())
                    .getAsJsonObject();
                jsonArray.add(jsonGame);
            }

            jsonObj.add("games", jsonArray);

            return jsonObj.toString();
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
    }


    // tests for BD //

    private static void storeUsers1(EntityManager entityManager) {
        UserService userService = new UserService(entityManager);

        if(userService.listAll().isEmpty()) {
            for(int i = 1; i < 5; i++) {
                User u = User.create("username" + i).email("user" + i + "@mail.com").password("qwerty123").build();
                userService.persist(u);
            }
        }
    }

    private static void storeGames1(EntityManager entityManager) {
        GameService gameService = new GameService(entityManager);
        ShelfService shelfService = new ShelfService(entityManager);
        UserService userService = new UserService(entityManager);

        User user = User.create("IOwnAShelf").email("shelves@mail.com").password("1234").build();
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

        if (gameService.listAll().isEmpty() && shelfService.listAll().isEmpty()) {
            userService.persist(user);
            gameService.persist(game1);
            gameService.persist(game2);
            gameService.persist(game3);
            shelfService.persist(shelf);
        }

        shelfService.addGame(shelf, user, game1);
        shelfService.addGame(shelf, user, game3);
    }

    private static void storeTags1(EntityManager entityManager) {
        TagService tagService = new TagService(entityManager);
        GameService gameService = new GameService(entityManager);

        if(tagService.listAll().isEmpty()) {
            for(int i = 1; i < 5; i++) {
                Tag t = new Tag("tag" + i);
                tagService.persist(t);
            }
        }

        List<Tag> tags = tagService.listAll();

        Game game1 = Game
                .create("tagged game 1")
                .description("a game with 1 tag")
                .releaseDate(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS))
                .lastUpdate(LocalDateTime.now().plusDays(2))
                .build();
        Game game2 = Game
                .create("tagged game 2")
                .description("a game with 3 tags")
                .releaseDate(LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.SECONDS))
                .lastUpdate(LocalDateTime.now().plusDays(3))
                .build();
        gameService.persist(game1);
        gameService.persist(game2);

        gameService.addTag(game1, tags.get(0));

        gameService.addTag(game2, tags.get(2));
        gameService.addTag(game2, tags.get(3));
    }
    
    private static void storeReviews1(EntityManager entityManager) {
        ReviewService reviewService = new ReviewService(entityManager);
        UserService userService = new UserService(entityManager);
        GameService gameService = new GameService(entityManager);
    
        if (reviewService.listAll().isEmpty()) {
            User u1 = User.create("i made 1 review").email("review1@mail.com").password("qwerty123").build();
            User u2 = User.create("i made 3 reviews").email("review3@mail.com").password("qwerty123").build();
            userService.persist(u1);
            userService.persist(u2);
    
            Game g1 = Game.create("game with 2 user reviews").description("the game has 2 reviews each by different users").releaseDate(LocalDateTime.now().plusDays(11).truncatedTo(ChronoUnit.SECONDS)).build();
            Game g2 = Game.create("game reviewd by same user").description("the game has 2 reviews by the same user").releaseDate(LocalDateTime.now().plusDays(13).truncatedTo(ChronoUnit.SECONDS)).build();
            gameService.persist(g1);
            gameService.persist(g2);
            
            Review r1 = new Review("this game is subpar if i say so myself");
            Review r2 = new Review("awesome game 10/10 would recommend i love it");
            reviewService.addReview(r1, u1, g1);
            reviewService.addReview(r2, u2, g1);
            
            Review r3 = new Review("a review");
            Review r4 = new Review("a second review");
            reviewService.addReview(r3, u2, g2);
            reviewService.addReview(r4, u2, g2);
            
            reviewService.likeReview(r1, u1);   // se auto-likeo
            reviewService.dislikeReview(r1, u2);
            
            reviewService.dislikeReview(r2, u1);
            
            reviewService.likeReview(r3, u1);
            reviewService.likeReview(r4, u1);
        }
    }
}

