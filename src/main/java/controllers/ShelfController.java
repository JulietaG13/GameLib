package controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import values.ErrorMessages;
import values.Token;
import interfaces.Controller;
import model.Game;
import model.Shelf;
import model.User;
import repositories.GameRepository;
import repositories.ShelfRepository;
import repositories.UserRepository;
import services.AccessControlService;
import services.ShelfService;
import spark.Spark;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ShelfController implements Controller {
    private static final String ROUTE_GET_ALL = "/shelf/all";
    private static final String ROUTE_GET_FROM_USER = "/shelf/get/user/:username/:max";     // header: token
    private static final String ROUTE_GET_FROM_SHELF = "/shelf/get/:id/:max";
    private static final String ROUTE_ADD_SHELF = "/shelf/add";                             // header: token
    private static final String ROUTE_DELETE_SHELF = "/shelf/delete/:shelf_id";             // header: token
    private static final String ROUTE_ADD_GAME = "/shelf/add/:shelf_id/:game_id";           // header: token
    private static final String ROUTE_REMOVE_GAME = "/shelf/remove/:shelf_id/:game_id";     // header: token

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
        setRouteGetAll();
        setRouteGetFromUser();
        setRouteGetFromShelf();
        setRouteAddShelf();
        setRouteAddGame();
        setRouteRemoveGame();
    }

    private void setRouteGetAll() {
        Spark.get(ROUTE_GET_ALL, "application/json", (req, resp) -> {
            EntityManager em = factory.createEntityManager();
            ShelfRepository service = new ShelfRepository(em);
            List<Shelf> allShelves = service.listAll();

            resp.status(200);
            resp.type("application/json");

            JsonArray array = new JsonArray();
            allShelves.forEach(s -> array.add(s.asJson()));

            em.close();
            return array.toString();
        });
    }

    private void setRouteGetFromUser() {
        Spark.get(ROUTE_GET_FROM_USER, "application/json", (req, resp) -> { // :username, :max  | header: token
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
                resp.status(400);
                return ErrorMessages.informationNotNumber("Max number of games");
            }

            // the one with the profile
            String username = req.params(":username");
            if (username == null || username.isEmpty()) {
                resp.status(403);
                return ErrorMessages.informationNotProvided("username");
            }
            UserRepository userRepository = new UserRepository(em);
            Optional<User> owner = userRepository.findByUsername(username);
            if (owner.isEmpty()) {
                resp.status(404);
                return ErrorMessages.usernameNotFound(username);
            }
            
            // all user's shelves
            ShelfRepository shelfRepository = new ShelfRepository(em);
            List<Shelf> shelves = shelfRepository.listByUser(owner.get());
            
            // the one asking
            String token = req.headers(Token.PROPERTY_NAME);
            if (token == null || !AccessControlService.isTokenValid(token)) {
                shelves = shelves.stream()
                    .filter(s -> !s.isPrivate())
                    .collect(Collectors.toList());
            } else {
                String clientUsername = AccessControlService.getUsernameFromToken(token);
                Optional<User> client = userRepository.findByUsername(clientUsername);
                if (client.isEmpty()) {
                    resp.status(404);
                    return ErrorMessages.usernameNotFound(clientUsername);
                }
                
                // get filtered shelves
                boolean canView = ShelfService.canViewPrivateShelves(owner.get(), client.get());
                if (!canView) {
                    shelves = shelves.stream()
                        .filter(s -> !s.isPrivate())
                        .collect(Collectors.toList());
                }
            }
            
            // check max
            if (max < shelves.size()) {
                shelves = shelves.subList(0, max);
            }
            
            resp.status(200);
            resp.type("application/json");

            JsonArray array = new JsonArray();
            shelves.forEach(s -> array.add(s.asJson()));

            em.close();
            return array.toString();
        });
    }
    
    private void setRouteGetFromShelf() {
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
                resp.status(400);
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
                resp.status(400);
                return ErrorMessages.informationNotNumber("Shelf ID");
            }
            
            ShelfRepository shelfRepository = new ShelfRepository(em);
            Optional<Shelf> shelf = shelfRepository.findById(id);
            
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
    
    private void setRouteAddShelf() {
        Spark.post(ROUTE_ADD_SHELF, "application/json", (req, resp) -> { // body: name, is_private | header: token
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
            
            JsonObject body = JsonParser
                .parseString(req.body())
                .getAsJsonObject();
            String shelfName = body.get("name").getAsString();
    
            boolean isPrivate;
            try {
                isPrivate = body.get("is_private").getAsBoolean();
            } catch (Exception e) {
                resp.status(400);
                return ErrorMessages.informationNotBoolean("is_private");
            }
            
            ShelfRepository shelfRepository = new ShelfRepository(em);
            List<Shelf> shouldBeEmpty = shelfRepository
                .listByUser(user.get())
                .stream()
                .filter(s -> s.getName().equals(shelfName))
                .collect(Collectors.toList());
            if (shouldBeEmpty.size() > 0) {
                resp.status(400);
                return "You already have a Shelf named: " + shelfName;
            }
            
            Shelf shelf = new Shelf(user.get(), shelfName, isPrivate);
            shelfRepository.persist(shelf);
            
            // fix
            Game game = new GameRepository(em).listAll().get(0);
            shelfRepository.addGame(shelf, user.get(), game);
            shelfRepository.takeOutGame(shelf, user.get(), game);
            // ---
            
            resp.status(200);
            resp.type("application/json");
    
            em.close();
            return shelf.asJson();
        });
    }
    
    private void setRouteAddGame() {
        Spark.put(ROUTE_ADD_GAME, "application/json", (req, resp) -> { // :shelf_id, :game_id | header: token
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
            
            long shelfId;
            try {
                String idStr = req.params(":shelf_id");
                shelfId = Long.parseLong(idStr);
            } catch (NumberFormatException e) {
                resp.status(400);
                return ErrorMessages.informationNotNumber("Shelf ID");
            }
    
            ShelfRepository shelfRepository = new ShelfRepository(em);
            Optional<Shelf> shelf = shelfRepository.findById(shelfId);
            if (shelf.isEmpty()) {
                resp.status(404);
                return ErrorMessages.informationNotFound("Shelf");
            }
            
            User shelfOwner = shelf.get().getOwner();
            if (!shelfOwner.getId().equals(user.get().getId())) {
                resp.status(403);
                return ErrorMessages.userNotAllowedToPerformAction();
            }
    
            long gameId;
            try {
                String idStr = req.params(":game_id");
                gameId = Long.parseLong(idStr);
            } catch (NumberFormatException e) {
                resp.status(400);
                return ErrorMessages.informationNotNumber("Shelf ID");
            }
    
            GameRepository gameRepository = new GameRepository(em);
            Optional<Game> game = gameRepository.findById(gameId);
            if (game.isEmpty()) {
                resp.status(404);
                return ErrorMessages.informationNotFound("Game");
            }
            
            shelfRepository.addGame(shelf.get(), user.get(), game.get());
            
            resp.status(200);
            resp.type("application/json");
            
            JsonObject jsonShelf = shelf.get().asJson();
            
            em.close();
            return jsonShelf;
        });
    }
    
    private void setRouteRemoveGame() {
        Spark.put(ROUTE_REMOVE_GAME, "application/json", (req, resp) -> { // :shelf_id, :game_id | header: token
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
            
            long shelfId;
            try {
                String idStr = req.params(":shelf_id");
                shelfId = Long.parseLong(idStr);
            } catch (NumberFormatException e) {
                resp.status(400);
                return ErrorMessages.informationNotNumber("Shelf ID");
            }
            
            ShelfRepository shelfRepository = new ShelfRepository(em);
            Optional<Shelf> shelf = shelfRepository.findById(shelfId);
            if (shelf.isEmpty()) {
                resp.status(404);
                return ErrorMessages.informationNotFound("Shelf");
            }
            
            User shelfOwner = shelf.get().getOwner();
            if (!shelfOwner.getId().equals(user.get().getId())) {
                resp.status(403);
                return ErrorMessages.userNotAllowedToPerformAction();
            }
            
            long gameId;
            try {
                String idStr = req.params(":game_id");
                gameId = Long.parseLong(idStr);
            } catch (NumberFormatException e) {
                resp.status(400);
                return ErrorMessages.informationNotNumber("Shelf ID");
            }
            
            GameRepository gameRepository = new GameRepository(em);
            Optional<Game> game = gameRepository.findById(gameId);
            if (game.isEmpty()) {
                resp.status(404);
                return ErrorMessages.informationNotFound("Game");
            }
            
            shelfRepository.takeOutGame(shelf.get(), user.get(), game.get());
            
            resp.status(200);
            resp.type("application/json");
            
            JsonObject jsonShelf = shelf.get().asJson();
            
            em.close();
            return jsonShelf;
        });
    }
}
