package controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import interfaces.Controller;
import model.Game;
import model.Tag;
import model.User;
import repositories.GameRepository;
import repositories.TagRepository;
import repositories.UserRepository;
import spark.Spark;
import values.ErrorMessages;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommonController implements Controller {
  private static final String ROUTE_SEARCH_ALL = "/common/search/all/:str";
  
  private final EntityManagerFactory factory;
  private static Controller instance;
  
  public static Controller getInstance(EntityManagerFactory factory) {
    if (instance == null) {
      instance = new CommonController(factory);
    }
    return instance;
  }
  
  private CommonController(EntityManagerFactory factory) {
    this.factory = factory;
  }
  
  @Override
  public void run() {
    setRouteSearchAll();
  }
  
  private void setRouteSearchAll() {
    Spark.get(ROUTE_SEARCH_ALL, "application/json", (req, resp) -> {
      EntityManager em = factory.createEntityManager();
      
      String searching = req.params("str");
      if (searching == null) {
        resp.status(400);
        return ErrorMessages.informationNotProvided("Search");
      }
  
      List<User> users = new UserRepository(em).findByUsernameLike(searching);
      List<Game> games = new GameRepository(em).findByNameLike(searching);
      List<Game> taggedGames = new ArrayList<>();
      
      TagRepository tagRepository = new TagRepository(em);
      Optional<Tag> tag = tagRepository.findByName(searching);
      if (tag.isPresent()) {
        taggedGames = new ArrayList<>(tag.get().getTaggedGames());
      }
      
      resp.type("application/json");
      resp.status(200);
  
      JsonArray userArray = new JsonArray();
      users.forEach(u -> userArray.add(u.asJsonProfile()));
  
      JsonArray gameArray = new JsonArray();
      games.forEach(g -> gameArray.add(g.asJson()));
  
      JsonArray taggedArray = new JsonArray();
      taggedGames.forEach(g -> taggedArray.add(g.asJson()));
  
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("searching", searching);
      jsonObject.add("users", userArray);
      jsonObject.add("games", gameArray);
      jsonObject.add("taggedGames", taggedArray);
      
      em.close();
      return jsonObject;
    });
  }
  
}
