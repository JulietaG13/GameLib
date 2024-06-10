package controllers;

import com.google.gson.JsonObject;
import interfaces.Controller;
import model.User;
import repositories.UserRepository;
import services.AccessControlService;
import services.UserService;
import spark.Spark;
import values.ErrorMessages;
import values.Token;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Optional;

public class AdminController implements Controller {
  private static final String ROUTE_IS_BANNED = "/admin/isbanned/:user_id";   // header: token
  private static final String ROUTE_BAN_USER = "/admin/ban/:user_id";         // header: token
  private static final String ROUTE_UNBAN_USER = "/admin/unban/:user_id";     // header: token
  
  private final EntityManagerFactory factory;
  private static Controller instance;
  
  public static Controller getInstance(EntityManagerFactory factory) {
    if (instance == null) {
      instance = new AdminController(factory);
    }
    return instance;
  }
  
  private AdminController(EntityManagerFactory factory) {
    this.factory = factory;
  }
  
  @Override
  public void run() {
    setRouteIsBanned();
    setRouteBanUser();
    setRouteUnbanUser();
  }
  
  private void setRouteIsBanned() {
    Spark.get(ROUTE_IS_BANNED, "application/json", (req, resp) -> {
      EntityManager em = factory.createEntityManager();
  
      String token = req.headers(Token.PROPERTY_NAME);
      if (token == null || !AccessControlService.isTokenValid(token)) {
        resp.status(401);
        return ErrorMessages.userMustBeLoggedIn();
      }
      String username = AccessControlService.getUsernameFromToken(token);
      Optional<User> admin = new UserRepository(em).findByUsername(username);
  
      if (admin.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("User");
      }
  
      if (!UserService.canBan(admin.get())) {
        resp.status(403);
        return ErrorMessages.userNotAllowedToPerformAction();
      }
  
      long userId;
      try {
        userId = Long.parseLong(req.params(":user_id"));
      } catch (NumberFormatException e) {
        resp.status(403);
        return ErrorMessages.informationNotNumber("User ID");
      }
  
      UserRepository userRepository = new UserRepository(em);
      Optional<User> user = userRepository.findById(userId);
  
      if (user.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("User");
      }
      
      resp.type("application/json");
      resp.status(200);
      
      boolean isBanned = user.get().isBanned();
      
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("is_banned", isBanned);
      
      em.close();
      return jsonObject;
    });
  }
  
  private void setRouteBanUser() {
    Spark.put(ROUTE_BAN_USER, "application/json", (req, resp) -> {
      EntityManager em = factory.createEntityManager();
      
      String token = req.headers(Token.PROPERTY_NAME);
      if (token == null || !AccessControlService.isTokenValid(token)) {
        resp.status(401);
        return ErrorMessages.userMustBeLoggedIn();
      }
      String username = AccessControlService.getUsernameFromToken(token);
      Optional<User> admin = new UserRepository(em).findByUsername(username);
      
      if (admin.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("User");
      }
      
      if (!UserService.canBan(admin.get())) {
        resp.status(403);
        return ErrorMessages.userNotAllowedToPerformAction();
      }
      
      long userId;
      try {
        userId = Long.parseLong(req.params(":user_id"));
      } catch (NumberFormatException e) {
        resp.status(403);
        return ErrorMessages.informationNotNumber("User ID");
      }
      
      UserRepository userRepository = new UserRepository(em);
      Optional<User> user = userRepository.findById(userId);
      
      if (user.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("User");
      }
      
      resp.status(204);
      
      userRepository.ban(user.get());
      
      em.close();
      return "";
    });
  }
  
  private void setRouteUnbanUser() {
    Spark.put(ROUTE_UNBAN_USER, "application/json", (req, resp) -> {
      EntityManager em = factory.createEntityManager();
      
      String token = req.headers(Token.PROPERTY_NAME);
      if (token == null || !AccessControlService.isTokenValid(token)) {
        resp.status(401);
        return ErrorMessages.userMustBeLoggedIn();
      }
      String username = AccessControlService.getUsernameFromToken(token);
      Optional<User> admin = new UserRepository(em).findByUsername(username);
      
      if (admin.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("User");
      }
      
      if (!UserService.canBan(admin.get())) {
        resp.status(403);
        return ErrorMessages.userNotAllowedToPerformAction();
      }
      
      long userId;
      try {
        userId = Long.parseLong(req.params(":user_id"));
      } catch (NumberFormatException e) {
        resp.status(403);
        return ErrorMessages.informationNotNumber("User ID");
      }
      
      UserRepository userRepository = new UserRepository(em);
      Optional<User> user = userRepository.findById(userId);
      
      if (user.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("User");
      }
      
      resp.status(204);
      
      userRepository.unban(user.get());
      
      em.close();
      return "";
    });
  }
}
