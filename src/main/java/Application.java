import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entities.responses.GameResponse;
import entities.responses.MessageResponse;
import entities.responses.StatusResponse;
import entities.Token;
import entities.responses.UserResponse;
import model.Game;
import model.Rol;
import model.Shelf;
import model.User;
import persistence.Database;
import services.AccessControlService;
import services.GameService;
import services.ShelfService;
import services.UserService;
import spark.Spark;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
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

        Spark.get("/users", "application/json", (req, resp) -> {

            resp.type("application/json");
            resp.status(201);

            UserService userService = new UserService(em);

            return gson.toJson(userService.listAll());
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
            final Game game = Game.fromJson(req.body());
            final GameService games = new GameService(em);
    
            //TODO(just to test for now)
            game.setReleaseDate(LocalDateTime.now());
            game.setLastUpdate(LocalDateTime.now());
            //
            
            MessageResponse titleResponse = Game.isTitleValid(game.getTitle());
            if (titleResponse.hasError()) {
                resp.status(404);
                return titleResponse.getMessage();
            }
    
            MessageResponse descriptionResponse = Game.isDescriptionValid(game.getDescription());
            if (descriptionResponse.hasError()) {
                resp.status(404);
                return descriptionResponse.getMessage();
            }
            
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
            //TODO(get token from header and validate it)
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
        Game game1 = Game.create("awesome game").description("just an awesome game").releaseDate(LocalDateTime.now()).build();
        Game game2 = Game.create("another awesome game").description("just another awesome game").releaseDate(LocalDateTime.now().plusMonths(4)).build();
        Game game3 = Game.create("even another awesome game").description("also an awesome game").releaseDate(LocalDateTime.now().plusMonths(2)).build();

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
}

