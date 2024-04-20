import com.google.gson.Gson;
import entities.Response;
import entities.Token;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
import java.util.Date;

public class Application {

    private static final Gson gson = new Gson();
    private static final String SECRET_KEY = "12000dpi0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    // TODO(secret_key)

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

            Response response = AccessControlService.isFormatValid(user);
            if(response.hasError) {
                resp.status(response.statusCode);
                return response.message;
            }

            if (user.getRol() == null) {    // default
                user.setRol(Rol.USER);
            }

            response = AccessControlService.isUserAvailable(user, em);
            if(response.hasError) {
                resp.status(response.statusCode);
                return response.message;
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

            if (username == null) {
                resp.status(404);
                return "Username cannot be null!";
            }

            if (password == null) {
                resp.status(404);
                return "Password cannot be null!";
            }

            Response response = AccessControlService.authenticateUser(username, password, em);
            if(response.hasError) {
                resp.status(response.statusCode);
                return response.message;
            }
            resp.status(200);

            // Generate JWT token
            Token token = generateToken(username);

            // Send token to frontend
            resp.type("application/json");
            return token.asJson();
        });

        Spark.post("/newgame", "application/json", (req, resp) -> {
            final Game game = Game.fromJson(req.body());
            final GameService games = new GameService(em);

            games.persist(game);
            resp.type("application/json");
            resp.status(201);

            return "Videogame saved successfully!";
        });

        Spark.get("/getgame/:id", "application/json", (req, resp) -> {
            final GameService games = new GameService(em);

            Optional<Game> game = games.findById(Long.valueOf(req.params(":id")));

            if(game.isEmpty()) {
                throw new IllegalArgumentException("There's no game with id " + req.params(":id"));
            }

            resp.type("application/json");
            resp.status(201);

            return game.get().asJson();
        });

        Spark.put("/editgame/:id", "application/json", (req, resp) -> {
            final GameService games = new GameService(em);

            Optional<Game> gameToEdit = games.findById(Long.valueOf(req.params(":id")));

            if(gameToEdit.isEmpty()) {
                throw new IllegalArgumentException("There's no game with id " + req.params(":id"));
            }

            Game game = gameToEdit.get();

//            EntityTransaction tx = em.getTransaction();
//            tx.begin();
//            games.persist(game);
//            resp.type("application/json");
//            resp.status(201);
//            tx.commit();
//            em.close();

            return game.asJson();
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

    //

    private static Token generateToken(String username) {
        // Create JWT token with username as subject
        String str = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + 3_600_000)) // 1 hour expiration
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
        return new Token(str);
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
        Game game1 = Game.create("awesome game").description("just an awesome game").build();
        Game game2 = Game.create("another awesome game").description("just another awesome game").build();
        Game game3 = Game.create("even another awesome game").description("also an awesome game").build();

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

