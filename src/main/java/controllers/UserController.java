package controllers;

import com.google.gson.JsonArray;
import interfaces.Controller;
import model.User;
import services.UserService;
import spark.Spark;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class UserController implements Controller {
    private static final String ROUTE_GET_ALL = "/user/all";
    private static final String ROUTE_GET_USER = "/user/:username";
    private static final String ROUTE_GET_PROFILE = "/user/profile/:username";
    private static final String ROUTE_DELETE = "/user/delete/:username";
    private static final String ROUTE_CREATE = "/user/create";
    private static final String ROUTE_LOGIN = "/user/login";

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
    }

    private void routeGetAll() {
        Spark.get(ROUTE_GET_ALL, "application/json", (req, resp) -> {

            resp.type("application/json");
            resp.status(200);

            EntityManager em = factory.createEntityManager();
            UserService userService = new UserService(em);

            JsonArray jsonArray = new JsonArray();
            for (User user : userService.listAll()) {
                jsonArray.add(user.asJson());
            }

            em.close();
            return jsonArray.toString();
        });
    }
}
