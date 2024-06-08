package controllers;

import com.google.gson.JsonArray;
import values.TagType;
import interfaces.Controller;
import model.Tag;
import repositories.TagRepository;
import spark.Spark;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class TagController implements Controller {
  private static final String ROUTE_GET_ALL = "/tag/get";
  private static final String ROUTE_GET_GENRES = "/tag/get/genres";
  
  private EntityManagerFactory factory;
  private static Controller instance;
  
  private TagController(EntityManagerFactory factory) {
    this.factory = factory;
  }
  
  public static Controller getInstance(EntityManagerFactory factory) {
    if (instance == null) {
      instance = new TagController(factory);
    }
    return instance;
  }
  
  @Override
  public void run() {
    routeGetAll();
    routeGetGenres();
  }
  
  private void routeGetAll() {
    Spark.get(ROUTE_GET_ALL, "application/json", (req, resp) -> {
      EntityManager em = factory.createEntityManager();
    
      resp.type("application/json");
      resp.status(201);
    
      TagRepository tagRepository = new TagRepository(em);
      JsonArray jsonArray = new JsonArray();
      for (Tag tag : tagRepository.listAll()) {
        jsonArray.add(tag.asJson());
      }
    
      em.close();
      return jsonArray;
    });
  }
  
  private void routeGetGenres() {
    Spark.get(ROUTE_GET_GENRES, "application/json", (req, resp) -> {
      EntityManager em = factory.createEntityManager();
    
      resp.type("application/json");
      resp.status(201);
    
      TagRepository tagRepository = new TagRepository(em);
      JsonArray jsonArray = new JsonArray();
      for (Tag tag : tagRepository.listAllType(TagType.GENRE)) {
        jsonArray.add(tag.asJsonWithoutGames());
      }
    
      em.close();
      return jsonArray;
    });
  }
}
