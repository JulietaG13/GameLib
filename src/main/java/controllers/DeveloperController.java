package controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import interfaces.Controller;
import model.Developer;
import model.Game;
import model.User;
import repositories.DeveloperRepository;
import repositories.GameRepository;
import repositories.UserRepository;
import services.AccessControlService;
import spark.Spark;
import values.ErrorMessages;
import values.Rol;
import values.Token;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DeveloperController implements Controller {
  private static final String ROUTE_IS_SUBSCRIBED = "/dev/subs/is/:user_id";         // header: token
  private static final String ROUTE_SUBSCRIBE = "/dev/subs/subscribe/:user_id";      // header: token
  private static final String ROUTE_UNSUBSCRIBE = "/dev/subs/unsubscribe/:user_id";  // header: token
  private static final String ROUTE_GET_DEVELOPED = "/dev/get/developed/:username";
  
  private final EntityManagerFactory factory;
  private static Controller instance;
  
  public static Controller getInstance(EntityManagerFactory factory) {
    if (instance == null) {
      instance = new DeveloperController(factory);
    }
    return instance;
  }
  
  private DeveloperController(EntityManagerFactory factory) {
    this.factory = factory;
  }
  
  @Override
  public void run() {
    setRouteIsSubscribed();
    setRouteSubscribe();
    setRouteUnsubscribe();
    setRouteGetDeveloped();
  }
  
  private void setRouteIsSubscribed() {
    Spark.get(ROUTE_IS_SUBSCRIBED, "application/json", (req, resp) -> {
      EntityManager em = factory.createEntityManager();
      
      String token = req.headers(Token.PROPERTY_NAME);
      if (token == null || !AccessControlService.isTokenValid(token)) {
        resp.status(401);
        return ErrorMessages.userMustBeLoggedIn();
      }
      String username = AccessControlService.getUsernameFromToken(token);
      Optional<User> user = new UserRepository(em).findByUsername(username);
      
      if (user.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("User");
      }
      
      long userId;
      try {
        userId = Long.parseLong(req.params(":user_id"));
      } catch (NumberFormatException e) {
        resp.status(403);
        return ErrorMessages.informationNotNumber("User ID");
      }
  
      DeveloperRepository developerRepository = new DeveloperRepository(em);
      Optional<Developer> dev = developerRepository.findByUserId(userId);
      
      if (dev.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("Developer");
      }
      
      resp.type("application/json");
      resp.status(200);
      
      boolean isSubscribed = dev.get().getSubscribers().contains(user.get());
      
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("is_subscribed", isSubscribed);
      
      em.close();
      return jsonObject;
    });
  }
  
  private void setRouteSubscribe() {
    Spark.post(ROUTE_SUBSCRIBE, "application/json", (req, resp) -> {
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
  
      long userId;
      try {
        userId = Long.parseLong(req.params(":user_id"));
      } catch (NumberFormatException e) {
        resp.status(403);
        return ErrorMessages.informationNotNumber("User ID");
      }
  
      DeveloperRepository developerRepository = new DeveloperRepository(em);
      Optional<Developer> dev = developerRepository.findByUserId(userId);
  
      if (dev.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("Developer");
      }
      
      resp.type("application/json");
      resp.status(204);
      
      userRepository.subscribe(user.get(), dev.get());
      
      em.close();
      return "";
    });
  }
  
  private void setRouteUnsubscribe() {
    Spark.post(ROUTE_UNSUBSCRIBE, "application/json", (req, resp) -> {
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
  
      long userId;
      try {
        userId = Long.parseLong(req.params(":user_id"));
      } catch (NumberFormatException e) {
        resp.status(403);
        return ErrorMessages.informationNotNumber("User ID");
      }
  
      DeveloperRepository developerRepository = new DeveloperRepository(em);
      Optional<Developer> dev = developerRepository.findByUserId(userId);
  
      if (dev.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("Developer");
      }
      
      resp.type("application/json");
      resp.status(204);
      
      userRepository.unsubscribe(user.get(), dev.get());
      
      em.close();
      return "";
    });
  }
  
  private void setRouteGetDeveloped() {
    Spark.get(ROUTE_GET_DEVELOPED, "application/json", (req, resp) -> {
      EntityManager em = factory.createEntityManager();
    
      String username = req.params(":username");
      
      UserRepository userRepository = new UserRepository(em);
      Optional<User> user = userRepository.findByUsername(username);
  
      if (user.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("Developer");
      }
      
      if (user.get().getRol() != Rol.DEVELOPER) {
        resp.status(400);
        return ErrorMessages.userNotDeveloper();
      }
    
      DeveloperRepository developerRepository = new DeveloperRepository(em);
      Optional<Developer> dev = developerRepository.findByUserId(user.get().getId());
    
      if (dev.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("Developer");
      }
    
      resp.type("application/json");
      resp.status(200);
      
      GameRepository gameRepository = new GameRepository(em);
  
      List<Game> developed = gameRepository.listByOwner(dev.get().getUser());
      JsonArray jsonArray = new JsonArray();
      developed.forEach(g -> jsonArray.add(g.asJson()));
      
      JsonObject jsonObject = new JsonObject();
      jsonObject.add("developed", jsonArray);
    
      em.close();
      return jsonObject;
    });
  }
}
