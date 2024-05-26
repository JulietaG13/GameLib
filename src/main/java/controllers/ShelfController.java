package controllers;

import com.google.gson.JsonArray;
import entities.ErrorMessages;
import interfaces.Controller;
import model.Game;
import model.Shelf;
import model.User;
import services.ShelfService;
import services.UserService;
import spark.Spark;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;

public class ShelfController implements Controller {
    private static final String ROUTE_GET_ALL = "/shelf/all";
    private static final String ROUTE_GET_FROM_USER = "/shelf/get/user/:username/:max";
    private static final String ROUTE_GET_FROM_SHELF = "/shelf/get/:id/:max";

    private EntityManagerFactory factory;
    private static ShelfController instance;

    private ShelfController(EntityManagerFactory factory) {
        this.factory = factory;
    }

    public static ShelfController getInstance(EntityManagerFactory factory) {
        if (instance == null) {
            instance = new ShelfController(factory);
        }
        return instance;
    }

    public void run() {
        routeGetAll();
        routeGetFromUser();
        routeGetFromShelf();
    }

    private void routeGetAll() {
        Spark.get(ROUTE_GET_ALL, "application/json", (req, resp) -> {
            EntityManager em = factory.createEntityManager();
            ShelfService service = new ShelfService(em);
            List<Shelf> allShelves = service.listAll();

            resp.status(200);
            resp.type("application/json");

            JsonArray array = new JsonArray();
            allShelves.forEach(s -> array.add(s.asJson()));

            em.close();
            return array.toString();
        });
    }

    private void routeGetFromUser() {
        Spark.get(ROUTE_GET_FROM_USER, "application/json", (req, resp) -> { // :username, :max
            EntityManager em = factory.createEntityManager();

            int max;
            try {
                String maxStr = req.params(":max");
                if (maxStr == null || maxStr.isEmpty()) {
                    max = 10;
                } else {
                    max = Integer.parseInt(maxStr);
                }
            } catch (NumberFormatException e) {
                resp.status(403);
                return ErrorMessages.informationNotNumber("Max number of games");
            }

            String username = req.params(":username");
            if (username == null || username.isEmpty()) {
                resp.status(403);
                return ErrorMessages.informationNotProvided("username");
            }
            UserService userService = new UserService(em);
            Optional<User> owner = userService.findByUsername(username);
            if (owner.isEmpty()) {
                resp.status(404);
                return ErrorMessages.usernameNotFound(username);
            }

            ShelfService shelfService = new ShelfService(em);
            List<Shelf> shelves = shelfService.listByUser(owner.get());

            resp.status(200);
            resp.type("application/json");

            JsonArray array = new JsonArray();
            shelves.forEach(s -> array.add(s.asJson()));

            em.close();
            return array.toString();
        });
    }
    
    private void routeGetFromShelf() {
        Spark.get(ROUTE_GET_FROM_SHELF, "application/json", (req, resp) -> { // :shelf_id, :max
            EntityManager em = factory.createEntityManager();
            
            int max;
            try {
                String maxStr = req.params(":max");
                if (maxStr == null || maxStr.isEmpty()) {
                    max = 10;
                } else {
                    max = Integer.parseInt(maxStr);
                }
            } catch (NumberFormatException e) {
                resp.status(403);
                return ErrorMessages.informationNotNumber("Max number of games");
            }
    
            long id;
            try {
                String idStr = req.params(":id");
                if (idStr == null || idStr.isEmpty()) {
                    id = 10;
                } else {
                    id = Integer.parseInt(idStr);
                }
            } catch (NumberFormatException e) {
                resp.status(403);
                return ErrorMessages.informationNotNumber("Shelf ID");
            }
            
            ShelfService shelfService = new ShelfService(em);
            Optional<Shelf> shelf = shelfService.findById(id);
            
            if (shelf.isEmpty()) {
                return ErrorMessages.informationNotFound("Shelf");
            }
            
            resp.status(200);
            resp.type("application/json");
            
            List<Game> games = shelf.get().getGames();
            if (max < games.size()) {
                games = games.subList(0, max);
            }
    
            JsonArray array = new JsonArray();
            games.forEach(g -> array.add(g.asJson()));
            
            em.close();
            return array.toString();
        });
    }
}
