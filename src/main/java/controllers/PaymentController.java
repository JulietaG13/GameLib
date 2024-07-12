package controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import interfaces.Controller;
import model.Developer;
import model.Notification;
import model.User;
import repositories.DeveloperRepository;
import repositories.NotificationRepository;
import repositories.UserRepository;
import services.AccessControlService;
import services.PaymentService;
import spark.Spark;
import values.ErrorMessages;
import values.Token;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Arrays;
import java.util.Optional;

public class PaymentController implements Controller {
  private static final String ROUTE_IS_DONATIONS_SETUP = "/pay/setup/is/:username";  // the dev's username
  private static final String ROUTE_SETUP_DONATIONS = "/pay/setup/set/:username";  // body: public_key        | header: token
  private static final String ROUTE_DISABLE_DONATIONS = "/pay/setup/disable/:username";  // body: public_key  | header: token
  private static final String ROUTE_CREATE_PREFERENCE = "/pay/pref/create/:username";  // body: amount, back_url
  private static final String ROUTE_SAVE_DONATION = "/pay/pref/save/:username";  // body: preference_id
  
  private final EntityManagerFactory factory;
  private static Controller instance;
  
  public static Controller getInstance(EntityManagerFactory factory) {
    if (instance == null) {
      instance = new PaymentController(factory);
    }
    return instance;
  }
  
  private PaymentController(EntityManagerFactory factory) {
    this.factory = factory;
  }
  
  @Override
  public void run() {
    setRouteIsDonationsSetup();
    setRouteSetupDonations();
    setRouteDisableDonations();
    setRouteCreatePreference();
    setRouteSaveDonation();
  }
  
  private void setRouteIsDonationsSetup() {
    Spark.get(ROUTE_IS_DONATIONS_SETUP, "application/json", (req, resp) -> {
      EntityManager em = factory.createEntityManager();
  
      String username = req.params(":username");
      UserRepository userRepository = new UserRepository(em);
      Optional<User> user = userRepository.findByUsername(username);
      if (user.isEmpty()) {
        resp.status(404);
        return ErrorMessages.usernameNotFound(username);
      }
  
      DeveloperRepository developerRepository = new DeveloperRepository(em);
      Optional<Developer> dev = developerRepository.findByUsername(username);
  
      if (dev.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("Developer");
      }
    
      resp.type("application/json");
      resp.status(200);
    
      JsonObject idObject = new JsonObject();
      idObject.addProperty("is_setup", dev.get().isDonationsSetup());
    
      em.close();
      return idObject;
    });
  }
  
  private void setRouteSetupDonations() {
    Spark.post(ROUTE_SETUP_DONATIONS, "application/json", (req, resp) -> {
      EntityManager em = factory.createEntityManager();
  
      String token = req.headers(Token.PROPERTY_NAME);
      if (token == null || !AccessControlService.isTokenValid(token)) {
        resp.status(401);
        return ErrorMessages.userMustBeLoggedIn();
      }
      String tokenUsername = AccessControlService.getUsernameFromToken(token);
      
      String username = req.params(":username");
      
      if (!tokenUsername.equalsIgnoreCase(username)) {
        resp.status(400);
        return "";
      }
      
      UserRepository userRepository = new UserRepository(em);
      Optional<User> user = userRepository.findByUsername(username);
      if (user.isEmpty()) {
        resp.status(404);
        return ErrorMessages.usernameNotFound(username);
      }
      
      DeveloperRepository developerRepository = new DeveloperRepository(em);
      Optional<Developer> dev = developerRepository.findByUsername(username);
      
      if (dev.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("Developer");
      }
      
      JsonObject body = JsonParser
          .parseString(req.body())
          .getAsJsonObject();
      
      String publicKey = body.get("public_key").getAsString();
      String accessToken = body.get("access_token").getAsString();
      
      developerRepository.setupDonations(dev.get().getUser(), publicKey, accessToken);
      
      resp.status(204);
      em.close();
      return "";
    });
  }
  
  private void setRouteDisableDonations() {
    Spark.post(ROUTE_DISABLE_DONATIONS, "application/json", (req, resp) -> {
      EntityManager em = factory.createEntityManager();
  
      String token = req.headers(Token.PROPERTY_NAME);
      if (token == null || !AccessControlService.isTokenValid(token)) {
        resp.status(401);
        return ErrorMessages.userMustBeLoggedIn();
      }
      String tokenUsername = AccessControlService.getUsernameFromToken(token);
  
      String username = req.params(":username");
  
      if (!tokenUsername.equalsIgnoreCase(username)) {
        resp.status(400);
        return "";
      }
  
      UserRepository userRepository = new UserRepository(em);
      Optional<User> user = userRepository.findByUsername(username);
      if (user.isEmpty()) {
        resp.status(404);
        return ErrorMessages.usernameNotFound(username);
      }
      
      DeveloperRepository developerRepository = new DeveloperRepository(em);
      Optional<Developer> dev = developerRepository.findByUsername(username);
      
      if (dev.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("Developer");
      }
      
      developerRepository.removeDonations(dev.get().getUser());
      
      resp.status(204);
      em.close();
      return "";
    });
  }
  
  private void setRouteCreatePreference() {
    Spark.post(ROUTE_CREATE_PREFERENCE, "application/json", (req, resp) -> {
      EntityManager em = factory.createEntityManager();
      
      JsonObject body = JsonParser
          .parseString(req.body())
          .getAsJsonObject();
      
      String amount;
      try {
        amount = body.get("amount").getAsString();
        Long l = Long.parseLong(amount);
      } catch (Error e) {
        resp.status(401);
        return ErrorMessages.informationNotNumber("Amount");
      }
      
      String backUrl = body.get("back_url").getAsString();  // TODO(check url?)
      
      String username = req.params(":username");
      UserRepository userRepository = new UserRepository(em);
      Optional<User> user = userRepository.findByUsername(username);
      if (user.isEmpty()) {
        resp.status(404);
        return ErrorMessages.usernameNotFound(username);
      }
  
      DeveloperRepository developerRepository = new DeveloperRepository(em);
      Optional<Developer> dev = developerRepository.findByUsername(username);
      
      if (dev.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("Developer");
      }
      
      String preferenceID;
  
      try {
        preferenceID = PaymentService.createPreference(dev.get(), amount, backUrl);
      } catch (MPException | MPApiException e) {
        System.out.println();
        System.out.println(e.getMessage());
        System.out.println();
        System.out.println(Arrays.toString(e.getStackTrace()));
        PaymentService.notifyErrorToDeveloper(dev.get(), factory);
        resp.status(424);
        return "Something went wrong";
      }
    
      resp.type("application/json");
      resp.status(200);
    
      JsonObject idObject = new JsonObject();
      idObject.addProperty("public_key", dev.get().getMpPublicKey());
      idObject.addProperty("preference_id", preferenceID);
      
      em.close();
      return idObject;
    });
  }
  
  private void setRouteSaveDonation() {
    Spark.post(ROUTE_SAVE_DONATION, "application/json", (req, resp) -> {
      EntityManager em = factory.createEntityManager();
  
      JsonObject body = JsonParser
          .parseString(req.body())
          .getAsJsonObject();
  
      String preferenceId = body.get("preference_id").getAsString();
      
      String username = req.params(":username");
      UserRepository userRepository = new UserRepository(em);
      Optional<User> user = userRepository.findByUsername(username);
      if (user.isEmpty()) {
        resp.status(404);
        return ErrorMessages.usernameNotFound(username);
      }
  
      DeveloperRepository developerRepository = new DeveloperRepository(em);
      Optional<Developer> dev = developerRepository.findByUsername(username);
  
      if (dev.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("Developer");
      }
  
      long amount;
      try {
        amount = PaymentService.getAmountPayed(dev.get(), preferenceId);
      } catch (MPException | MPApiException e) {
        System.out.println();
        System.out.println(e.getMessage());
        System.out.println();
        System.out.println(Arrays.toString(e.getStackTrace()));
        PaymentService.notifyErrorToDeveloper(dev.get(), factory);
        resp.status(424);
        return "Something went wrong";
      }
      
      PaymentService.notifyDeveloper(dev.get(), preferenceId, amount, factory);
      
      resp.status(204);
      em.close();
      return "";
    });
  }
}
