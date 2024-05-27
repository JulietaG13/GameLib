package controllers;

import com.google.gson.JsonArray;
import entities.ErrorMessages;
import interfaces.Controller;
import model.Game;
import model.Tag;
import repositories.TagRepository;
import spark.Spark;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameController implements Controller {
  
  private static final String ROUTE_GET_BY_TAG = "/game/get/tag/:tag_id";
  
  private EntityManagerFactory factory;
  private static Controller instance;
  
  private GameController(EntityManagerFactory factory) {
    this.factory = factory;
  }
  
  public static Controller getInstance(EntityManagerFactory factory) {
    if (instance == null) {
      instance = new GameController(factory);
    }
    return instance;
  }
  
  @Override
  public void run() {
    routeGetByTag();
  }
  
  public void routeGetByTag() {
    Spark.get(ROUTE_GET_BY_TAG, "application/json", (req, resp) -> {
      EntityManager em = factory.createEntityManager();
      final TagRepository tagRepository = new TagRepository(em);
    
      long id;
      try {
        id = Long.parseLong(req.params(":tag_id"));
      } catch (NumberFormatException e) {
        resp.status(403);
        return ErrorMessages.informationNotNumber("Tag ID");
      }
    
      Optional<Tag> tag = tagRepository.findById(id);
    
      if (tag.isEmpty()) {
        resp.status(404);
        return ErrorMessages.informationNotFound("Tag");
      }
    
      resp.type("application/json");
      resp.status(201);
  
      List<Game> games = new ArrayList<>(tag.get().getTaggedGames());
  
      JsonArray jsonArray = new JsonArray();
      games.forEach(g -> jsonArray.add(g.asJson()));
      em.close();
    
      return jsonArray;
    });
  
  }
}
