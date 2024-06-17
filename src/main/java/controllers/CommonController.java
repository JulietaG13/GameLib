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

      // Search games
      HttpResponse<JsonNode> searchResponse = makeApiRequest("search", "search \"" + searching + "\"; fields *;");
      if (searchResponse.getStatus() != 200) {
        resp.status(searchResponse.getStatus());
        return searchResponse.getStatusText();
      }

      // Get game IDs
      JSONArray gamesArray = searchResponse.getBody().getArray();
      String gamesId = getCommaSeparatedIds(gamesArray);

      // Get covers
      HttpResponse<JsonNode> coversResponse = makeApiRequest("covers", "where game = (" + gamesId + "); fields game,url;");
      if (coversResponse.getStatus() != 200) {
        resp.status(coversResponse.getStatus());
        return coversResponse.getStatusText();
      }

      // Create a map of game ID to cover URL
      Map<Integer, String> coverMap = createCoverMap(coversResponse.getBody().getArray());

      // Get games data
      HttpResponse<JsonNode> gamesResponse = makeApiRequest("games", "where id = (" + gamesId + "); fields id,name,url;");
      if (gamesResponse.getStatus() != 200) {
        resp.status(gamesResponse.getStatus());
        return gamesResponse.getStatusText();
      }

      // Add cover URLs to games
      JSONArray gamesResultArray = gamesResponse.getBody().getArray();
      addCoversToGames(gamesResultArray, coverMap);

      resp.status(200);
      resp.type("application/json");
      return gamesResultArray.toString();
    });
  }

  private HttpResponse<JsonNode> makeApiRequest(String endpoint, String query) {
    return Unirest.post(API_URL + endpoint)
            .header("Client-ID", CLIENT_ID)
            .header("Authorization", ACCESS_TOKEN)
            .header("Accept", "application/json")
            .body(query)
            .asJson();
  }

  private String getCommaSeparatedIds(JSONArray jsonArray) {
    StringBuilder ids = new StringBuilder();
    for (int i = 0; i < jsonArray.length(); i++) {
      if (i > 0) {
        ids.append(",");
      }
      ids.append(jsonArray.getJSONObject(i).getInt("game"));
    }
    return ids.toString();
  }

  private Map<Integer, String> createCoverMap(JSONArray coversArray) {
    Map<Integer, String> coverMap = new HashMap<>();
    for (int i = 0; i < coversArray.length(); i++) {
      JSONObject coverObject = coversArray.getJSONObject(i);
      coverMap.put(coverObject.getInt("game"), coverObject.getString("url"));
    }
    return coverMap;
  }

  private void addCoversToGames(JSONArray gamesArray, Map<Integer, String> coverMap) {
    for (int i = 0; i < gamesArray.length(); i++) {
      JSONObject gameObject = gamesArray.getJSONObject(i);
      int gameId = gameObject.getInt("id");
      if (coverMap.containsKey(gameId)) {
        String coverUrl = coverMap.get(gameId).replace("t_thumb", "t_1080p"); // Get better quality cover
        gameObject.put("cover_url", coverUrl);
      }
    }
  }

}