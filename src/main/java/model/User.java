package model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entities.responses.MessageResponse;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class User {
  
  @Id
  @GeneratedValue(generator = "userGen", strategy = GenerationType.SEQUENCE)
  private Long id;
  
  @Column(nullable = false, unique = true)
  private String username;
  
  @Column(nullable = false, unique = true)
  private String email;
  
  @Column(nullable = false)
  private String password;
  
  @Column()
  private String biography;
  
  @Column(nullable = false)
  private Rol rol;
  
  @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
  private final Set<Review> reviews = new HashSet<>();
  
  @ManyToMany(mappedBy = "likedBy")
  private final Set<Review> likedReviews = new HashSet<>();
  
  @ManyToMany(mappedBy = "dislikedBy")
  private final Set<Review> dislikedReviews = new HashSet<>();
  
  @ManyToMany(mappedBy = "upvotes")
  private final Set<Game> upvotedGames = new HashSet<>();
  
  //TODO(pfp, banner)

    /*
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private final List<Publication> publications = new ArrayList<>();

    @ManyToMany(mappedBy = "liked")
    private final List<Publication> likes = new ArrayList<>();
    */
  
  public User() {}
  
  public static UserBuilder create(String username) {
    return new UserBuilder(username);
  }
  
  private User(UserBuilder builder) {
    this.username = builder.username;
    this.email = builder.email;
    this.password = builder.password;
    this.rol = builder.rol;
  }
  
  public static class UserBuilder {
    private final String username;
    private String email;
    private String password;
    private Rol rol;
    
    public UserBuilder(String username) {
      this.username = username;
    }
    
    public UserBuilder email(String email) {
      this.email = email;
      return this;
    }
    
    public UserBuilder password(String password) {
      this.password = password;
      return this;
    }
    
    public UserBuilder rol(Rol rol) {
      this.rol = rol;
      return this;
    }
    
    public User build() {
      if(rol == null) rol = Rol.USER;
      return new User(this);
    }
    
  }
  
  // JSON //
  
  public static User fromJson(String json) {
    final Gson gson = new Gson();
    return gson.fromJson(json, User.class);
  }
  
  public JsonObject asJson() {
    JsonObject jsonObj = new JsonObject();
    jsonObj.addProperty("id", id);
    jsonObj.addProperty("username", username);
    jsonObj.addProperty("email", email);
    jsonObj.addProperty("password", password);
    jsonObj.addProperty("biography", biography);
    jsonObj.addProperty("rol", rol.name());

    return jsonObj;
  }
  
  // RESTRICTIONS //
  
  public static MessageResponse isUsernameValid(String username) {
    if (username == null) {
      return new MessageResponse(true, "Username cannot be null!");
    }
    if (username.equals("")) {
      return new MessageResponse(true, "Username cannot be empty!");
    }
    return new MessageResponse(false);
  }
  
  public static MessageResponse isPasswordValid(String password) {
    if (password == null) {
      return new MessageResponse(true, "Password cannot be null!");
    }
    if (password.equals("")) {
      return new MessageResponse(true, "Password cannot be empty!");
    }
    return new MessageResponse(false);
  }
  
  public static MessageResponse isEmailValid(String email) {
    if (email == null) {
      return new MessageResponse(true, "Email cannot be null!");
    }
    if (email.equals("")) {
      return new MessageResponse(true, "Email cannot be empty!");
    }
    return new MessageResponse(false);
  }
  
  // ADDS? //
  
  public void addReview(Review review) {
    reviews.add(review);
    review.setAuthor(this);
  }
  
  public void likeReview(Review review) {
    likedReviews.add(review);
    if (!review.getLiked().contains(this)) {
      review.addLike(this);
    }
  }
  
  public void dislikeReview(Review review) {
    dislikedReviews.add(review);
    if (!review.getDisliked().contains(this)) {
      review.addDislike(this);
    }
  }
  
  public void addGameUpvote(Game game) {
    upvotedGames.add(game);
    if (!game.getUpvotes().contains(this)) {
      game.addUpvote(this);
    }
  }
  
  // GETTERS - SETTER //
  
  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public String getUsername() {
    return username;
  }
  
  public void setUsername(String username) {
    this.username = username;
  }
  
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    this.email = email;
  }
  
  public String getPassword() {
    return password;
  }
  
  public void setPassword(String password) {
    this.password = password;
  }
  
  public String getBiography() {
    return biography;
  }
  
  public void setBiography(String biography) {
    this.biography = biography;
  }
  
  public Rol getRol() {
    return rol;
  }
  
  public void setRol(Rol rol) {
    this.rol = rol;
  }
  
  public Set<Review> getLikedReviews() {
    return likedReviews;
  }
  
  public Set<Review> getDislikedReviews() {
    return dislikedReviews;
  }
  
  public Set<Game> getUpvotedGames() {
    return upvotedGames;
  }
  
  // OTHERS //
  
  @Override
  public boolean equals(Object obj) {
    if (obj.getClass() != User.class) {
      return false;
    }
    return Objects.equals(this.id, ((User) obj).id);
  }
}
