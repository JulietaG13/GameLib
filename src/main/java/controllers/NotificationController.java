package controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import interfaces.Controller;
import model.Notification;
import model.User;
import repositories.UserRepository;
import services.AccessControlService;
import spark.Spark;
import values.ErrorMessages;
import values.Token;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class NotificationController implements Controller {
  private static final String ROUTE_GET_FROM_USER = "/notif/get/user/all";
  
  private EntityManagerFactory factory;
  private static Controller instance;
  
  private NotificationController(EntityManagerFactory factory) {
    this.factory = factory;
  }
  
  public static Controller getInstance(EntityManagerFactory factory) {
    if (instance == null) {
      instance = new NotificationController(factory);
    }
    return instance;
  }
  
  
  @Override
  public void run() {
    setRouteGetFromUser();
  }
  
  private void setRouteGetFromUser() {
    Spark.get(ROUTE_GET_FROM_USER, "application/json", (req, resp) -> {
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
    
      resp.status(200);
      resp.type("application/json");
      
      List<Notification> notifications = new ArrayList<>(user.get().getNotifications());
      Collections.sort(notifications);
  
      JsonArray notificationsArray = new JsonArray();
      notifications.forEach(n -> notificationsArray.add(n.asJson()));
      
      JsonObject notificationsJson = new JsonObject();
      notificationsJson.add("notifications", notificationsArray);
      
      em.close();
      return notificationsJson;
    });
  }
}
