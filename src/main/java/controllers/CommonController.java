package controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import interfaces.Controller;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
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
import java.util.*;

public class CommonController implements Controller {

  private static final String ROUTE_SEARCH_ALL = "/common/search/all/:str";
  
  private final EntityManagerFactory factory;
  private static Controller instance;


  //Get from API: IGDB
  private static final String ROUTE_SEARCH_API = "/common/search/api/:str";
  private static final String API_URL = "https://api.igdb.com/v4/";
  private static final String CLIENT_ID = "bdcmejstsbgrhw51oazx55vgdc74cf";
  private static final String ACCESS_TOKEN = "Bearer vhy09lphz5sfo6kr6fwczn9cd23ren";
  
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
    setRouteSearchAllFromApi();
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


  private void setRouteSearchAllFromApi() {
    Spark.get(ROUTE_SEARCH_API, "application/json", (req, resp) -> {
      String searching = req.params("str");

      //Search games
      HttpResponse<JsonNode> searchResponse = Unirest.post(API_URL + "search")
              .header("Client-ID", CLIENT_ID)
              .header("Authorization", ACCESS_TOKEN)
              .header("Accept", "application/json")
              .body("search \"" + searching + "\"; fields alternative_name,character,checksum,collection,company,description,game,name,platform,published_at,test_dummy,theme;")
              .asJson();

      if (searchResponse.getStatus() != 200) {
        resp.status(searchResponse.getStatus());
        return searchResponse.getStatusText();
      }


      JSONArray gamesArray = searchResponse.getBody().getArray();
      StringBuilder idsQuery = new StringBuilder();
      for (int i = 0; i < gamesArray.length(); i++) {
        if (i > 0) {
          idsQuery.append(",");
        }
        idsQuery.append(gamesArray.getJSONObject(i).getInt("game"));
      }


      String coversQuery = "where game = (" + idsQuery + "); fields game,url;";

      //Get Covers from previous search
      HttpResponse<JsonNode> coversResponse = Unirest.post(API_URL + "covers")
              .header("Client-ID", CLIENT_ID)
              .header("Authorization", ACCESS_TOKEN)
              .header("Accept", "application/json")
              .body(coversQuery)
              .asJson();

      if (coversResponse.getStatus() != 200) {
        resp.status(coversResponse.getStatus());
        return coversResponse.getStatusText();
      }

      JSONArray coversArray = coversResponse.getBody().getArray();

      //Create map with covers
      Map<Integer, String> coversMap = new HashMap<>();
      for (int i = 0; i < coversArray.length(); i++) {
        JSONObject cover = coversArray.getJSONObject(i);
        String coverUrl = cover.getString("url").replace("t_thumb", "t_1080p"); // Get better quality cover
        coversMap.put(cover.getInt("game"), coverUrl);
      }

      //Add cover url to games
      for (int i = 0; i < gamesArray.length(); i++) {
        JSONObject game = gamesArray.getJSONObject(i);
        int gameId = game.getInt("game");
        if (coversMap.containsKey(gameId)) {
          game.put("cover_url", coversMap.get(gameId));
        }
      }

      resp.type("application/json");
      return gamesArray.toString();
    });
  }
  
}
