package model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Review {
  @Id
  @GeneratedValue
  private Long id;
  
  @Column(nullable = false)
  private String text;
  
  @ManyToOne
  private User author;
  
  @ManyToOne
  private Game game;
  
  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "liked_by",
      joinColumns = @JoinColumn(name = "review_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  private final Set<User> likedBy = new HashSet<>();
  
  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "disliked_by",
      joinColumns = @JoinColumn(name = "review_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  private final Set<User> dislikedBy = new HashSet<>();
  
  public Review() {}
  
  public Review(String text) {
    this.text = text;
  }
  
  // JSON //
  
  public static Review fromJson(String json) {
    JsonObject jsonObj = JsonParser
        .parseString(json)
        .getAsJsonObject();
    
    String _text = jsonObj.get("text").getAsString();
    Review review = new Review(_text);
    
    try {
      Long _id = jsonObj.get("id").getAsLong(); // optional
      review.setId(_id);
    } catch (Exception e) {
      // do nothing
    }
    
    return review;
  }
  
  public JsonObject asJson() {
    JsonObject jsonObj = new JsonObject();
    jsonObj.addProperty("id", id);
    jsonObj.addProperty("text", text);
    jsonObj.addProperty("author_id", author.getId());
    jsonObj.addProperty("game_id", game.getId());
    jsonObj.addProperty("likes", likedBy.size());
    jsonObj.addProperty("dislikes", dislikedBy.size());
    return jsonObj;
  }
  
  // ADDS? //
  
  public void addLike(User user) {
    likedBy.add(user);
    if (!user.getLikedReviews().contains(this)) {
      user.likeReview(this);
    }
  }
  
  public void addDislike(User user) {
    dislikedBy.add(user);
    if (!user.getDislikedReviews().contains(this)) {
      user.dislikeReview(this);
    }
  }
  
  // GETTERS - SETTERS //
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public Long getId() {
    return id;
  }
  
  public void setAuthor(User user) {
    author = user;
  }

  public User getAuthor() {
    return author;
  }
  
  public void setGame(Game game) {
    this.game = game;
  }

  public Game getGame() {
    return game;
  }
  
  public Set<User> getLiked() {
    return likedBy;
  }
  
  public Set<User> getDisliked() {
    return dislikedBy;
  }
  
  // OTHERS //
  
  @Override
  public boolean equals(Object obj) {
    if (obj.getClass() != Review.class) {
      return false;
    }
    return Objects.equals(this.id, ((Review) obj).id);
  }
}
