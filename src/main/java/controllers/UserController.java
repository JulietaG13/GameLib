package controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entities.ErrorMessages;
import interfaces.Controller;
import model.User;
import repositories.UserRepository;
import spark.Spark;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Optional;

public class UserController implements Controller {
    private static final String ROUTE_GET_ALL = "/user/all";
    private static final String ROUTE_GET_USER = "/user/:username";
    private static final String ROUTE_GET_PROFILE = "/user/profile/:username";
    private static final String ROUTE_DELETE = "/user/delete/:username";
    private static final String ROUTE_CREATE = "/user/create";
    private static final String ROUTE_LOGIN = "/user/login";
    private static final String ROUTE_PROFILE_EDIT_PFP = "/user/profile/:username/edit/pfp";
    private static final String ROUTE_PROFILE_EDIT_BANNER = "/user/profile/:username/edit/banner";

    private final EntityManagerFactory factory;
    private static UserController userController;

    public static UserController getInstance(EntityManagerFactory factory) {
        if (userController == null) {
            userController = new UserController(factory);
        }
        return userController;
    }

    private UserController(EntityManagerFactory factory) {
        this.factory = factory;
    }

    public void run() {
        routeGetAll();
        routeEditPfp();
        routeEditBanner();
    }

    private void routeGetAll() {
        Spark.get(ROUTE_GET_ALL, "application/json", (req, resp) -> {

            resp.type("application/json");
            resp.status(200);

            EntityManager em = factory.createEntityManager();
            UserRepository userRepository = new UserRepository(em);

            JsonArray jsonArray = new JsonArray();
            for (User user : userRepository.listAll()) {
                jsonArray.add(user.asJson());
            }

            em.close();
            return jsonArray.toString();
        });
    }
    
    private void routeEditPfp() {
        Spark.post(ROUTE_PROFILE_EDIT_PFP, "application/json", (req, resp) -> { // :username | body: pfp
            EntityManager em = factory.createEntityManager();
        
            String username = req.params(":username");
            UserRepository userRepository = new UserRepository(em);
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isEmpty()) {
                resp.status(404);
                return ErrorMessages.usernameNotFound(username);
            }
        
            JsonObject body = JsonParser
                .parseString(req.body())
                .getAsJsonObject();
            
            String pfp = body.get("pfp").getAsString();
            user.get().setPfp(pfp);
            userRepository.persist(user.get());
        
            em.close();
            return user.get().asJsonProfile();
        });
    }
    
    private void routeEditBanner() {
        Spark.post(ROUTE_PROFILE_EDIT_BANNER, "application/json", (req, resp) -> { // :username | body: banner
            EntityManager em = factory.createEntityManager();
            
            String username = req.params(":username");
            UserRepository userRepository = new UserRepository(em);
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isEmpty()) {
                resp.status(404);
                return ErrorMessages.usernameNotFound(username);
            }
            
            JsonObject body = JsonParser
                .parseString(req.body())
                .getAsJsonObject();
            
            String banner = body.get("banner").getAsString();
            user.get().setBanner(banner);
            userRepository.persist(user.get());
            
            em.close();
            return user.get().asJsonProfile();
        });
    }
}
