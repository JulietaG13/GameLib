import com.google.gson.Gson;
import model.Game;
import model.Shelf;
import model.User;
import persistence.Database;
import services.GameService;
import services.ShelfService;
import services.UserService;
import spark.Spark;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Optional;

public class Application {

    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        new Database().startDBServer();
        final EntityManagerFactory factory = Persistence.createEntityManagerFactory("gamelib");
        final EntityManager entityManager = factory.createEntityManager();

        Spark.port(4567);

        storeUsers1(entityManager);
        storeGames1(entityManager);

        Spark.get("/users", "application/json", (req, resp) -> {

            resp.type("application/json");
            resp.status(201);

            UserService userService = new UserService(entityManager);

            return gson.toJson(userService.listAll());
        });

        Spark.post("/newuser", "application/json", (req, resp) -> {
            final User user = User.fromJson(req.body());

            //TODO(try/catch constraint errors)
            final EntityManager em = factory.createEntityManager();
            final UserService userService = new UserService(em);
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            userService.persist(user);
            resp.type("application/json");
            resp.status(201);
            tx.commit();
            em.close();

            return user.asJson();
        });

        Spark.post("/loginuser", "application/json", (req, resp) -> {
            final User user = User.fromJson(req.body());

            if (user.getUsername() == null) {
                resp.status(404);
                return "Username cannot be null!";
            }

            if (user.getPassword() == null) {
                resp.status(404);
                return "Password cannot be null!";
            }

            final EntityManager em = factory.createEntityManager();
            final UserService userService = new UserService(em);

            EntityTransaction tx = em.getTransaction();
            tx.begin();

            Optional<User> possibleUser = userService.findByUsername(user.getUsername());
            if (possibleUser.isEmpty()) {
                resp.status(404);
                return "User does not exist!";
            }

            if (!possibleUser.get().getPassword().equals(user.getPassword())) {
                resp.status(404);
                return "Password is incorrect!";
            }

            resp.status(200);
            tx.commit();
            em.close();

            // TODO(return token in body)
            return "OK";
        });

        Spark.post("/newgame", "application/json", (req, resp) -> {
            final Game game = Game.fromJson(req.body());

            final EntityManager em = factory.createEntityManager();
            final GameService games = new GameService(em);
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            games.persist(game);
            resp.type("application/json");
            resp.status(201);
            tx.commit();
            em.close();

            return "Videogame saved successfully!";
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

    private static void storeUsers1(EntityManager entityManager) {
        final EntityTransaction transaction = entityManager.getTransaction();
        UserService userService = new UserService(entityManager);

        transaction.begin();
        if(userService.listAll().isEmpty()) {
            for(int i = 1; i < 5; i++) {
                User u = User.create("username" + i).email("user" + i + "@mail.com").password("qwerty123").build();
                userService.persist(u);
            }
        }
        transaction.commit();
    }

    private static void storeGames1(EntityManager entityManager) {
        final EntityTransaction transaction = entityManager.getTransaction();
        GameService gameService = new GameService(entityManager);
        ShelfService shelfService = new ShelfService(entityManager);
        UserService userService = new UserService(entityManager);

        User user = User.create("IOwnAShelf").email("shelves@mail.com").password("1234").build();
        Shelf shelf = new Shelf(user, "elf on a shelf");
        Game game1 = Game.create("awesome game").description("just an awesome game").build();
        Game game2 = Game.create("another awesome game").description("just another awesome game").build();
        Game game3 = Game.create("even another awesome game").description("also an awesome game").build();

        transaction.begin();
        if (gameService.listAll().isEmpty() && shelfService.listAll().isEmpty()) {
            userService.persist(user);
            gameService.persist(game1);
            gameService.persist(game2);
            gameService.persist(game3);
            shelfService.persist(shelf);
        }
        transaction.commit();

        shelfService.addGame(shelf, user, game1);
        shelfService.addGame(shelf, user, game3);
    }

    public static void clearTables(EntityManager entityManager) {
        final EntityTransaction t = entityManager.getTransaction();
        /*
        t.begin();
        new UserService(entityManager).deleteAll();
        new GameService(entityManager).deleteAll();
        t.commit();
        */
    }
}

