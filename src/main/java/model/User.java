package model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import services.EmailService;
import values.Rol;
import values.responses.ErrorResponse;
import values.responses.StatusResponse;
import interfaces.Responses;

import javax.persistence.*;
import java.util.Collections;
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
  
  private boolean isBanned = false;
  
  @OneToMany(mappedBy = "owner")
  private final Set<Notification> notifications = new HashSet<>();

  @OneToMany(mappedBy = "owner")
  private final Set<Game> developed = new HashSet<>();
  
  @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
  private final Set<Review> reviews = new HashSet<>();
  
  @ManyToMany(mappedBy = "likedBy")
  private final Set<Review> likedReviews = new HashSet<>();
  
  @ManyToMany(mappedBy = "dislikedBy")
  private final Set<Review> dislikedReviews = new HashSet<>();
  
  @ManyToMany(mappedBy = "upvotes")
  private final Set<Game> upvotedGames = new HashSet<>();

  @ManyToMany
  @JoinTable(
          name = "user_friends",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "friend_id")
  )
  private final Set<User> friends = new HashSet<>();

  @ManyToMany
  @JoinTable(
          name = "friend_requests_sent",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "request_sent_to_id")
  )
  private final Set<User> friendRequestsSent = new HashSet<>();

  @ManyToMany
  @JoinTable(
          name = "friend_requests_pending",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "request_pending_from_id")
  )
  private final Set<User> friendRequestsPending = new HashSet<>();
  
  @ManyToMany(mappedBy = "subscribers")
  private Set<Game> subscribedGames = new HashSet<>();

  @ManyToMany(mappedBy = "subscribers")
  private Set<Developer> subscribedDevelopers = new HashSet<>();

  @Lob
  @Column(columnDefinition = "CLOB")
  private String pfp;

  @Lob
  @Column(columnDefinition = "CLOB")
  private String banner;

  public User() {}
  
  public static UserBuilder create(String username) {
    return new UserBuilder(username);
  }

  public User(String username, String email, String password, Rol rol) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.rol = rol;
  }

  private User(UserBuilder builder) {
    this(builder.username, builder.email, builder.password, builder.rol);
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
    jsonObj.addProperty("rol", rol.name());
    jsonObj.addProperty("is_banned", isBanned);
    return jsonObj;
  }

  public JsonObject asJsonProfile() {
    JsonObject jsonObj = new JsonObject();
    jsonObj.addProperty("id", id);
    jsonObj.addProperty("username", username);
    jsonObj.addProperty("rol", rol.name());
    jsonObj.addProperty("biography", biography);
    jsonObj.addProperty("pfp", pfp);
    jsonObj.addProperty("banner", banner);
    jsonObj.addProperty("is_banned", isBanned);
    return jsonObj;
  }
  
  // RESTRICTIONS //
  
  public static Responses isUsernameValid(String username) {
    if (username == null) {
      return new ErrorResponse(404, "Username cannot be null!");
    }
    username = username.trim();
    if (username.isEmpty()) {
      return new ErrorResponse(404, "Username cannot be empty!");
    }
    return new StatusResponse(200);
  }
  
  public static Responses isPasswordValid(String password) {
    if (password == null) {
      return new ErrorResponse(404, "Password cannot be null!");
    }
    if (password.isEmpty()) {
      return new ErrorResponse(404, "Password cannot be empty!");
    }
    return new StatusResponse(200);
  }
  
  public static Responses isEmailValid(String email) {
    if (email == null) {
      return new ErrorResponse(404, "Email cannot be null!");
    }
    email = email.trim();
    if (!EmailService.isValidEmail(email)) {
      return new ErrorResponse(404, "Email format invalid!");
    }
    return new StatusResponse(200);
  }
  
  // UTILITY METHODS //

  public void addDeveloped(Game game) {
    if (this.rol != Rol.DEVELOPER) return; //throw new NoPermissionException("User not allowed to have developed games");
    developed.add(game);
    game.setOwner(this);
  }
  
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

  public void addFriend(User friend) {
    this.friends.add(friend);
    friend.getFriendsInternal().add(this);
  }

  public void removeFriend(User friend) {
    this.friends.remove(friend);
    friend.getFriendsInternal().remove(this);
  }

  public void sendFriendRequest(User user) {
    this.friendRequestsSent.add(user);
    user.getFriendRequestsPendingInternal().add(this);
  }

  public void acceptFriendRequest(User user) {
    if (this.friendRequestsPending.contains(user)) {
      this.friendRequestsPending.remove(user);
      user.getFriendRequestsSentInternal().remove(this);
      this.addFriend(user);
    }
  }

  public void rejectFriendRequest(User user) {
    if (this.friendRequestsPending.contains(user)) {
      this.friendRequestsPending.remove(user);
      user.getFriendRequestsSentInternal().remove(this);
    }
  }
  
  public void subscribe(Game game) {
    subscribedGames.add(game);
    if (!game.getSubscribers().contains(this)) {
      game.addSubscriber(this);
    }
  }

  public void subscribe(Developer developer) {
    subscribedDevelopers.add(developer);
    if (!developer.getSubscribers().contains(this)) {
      developer.addSubscriber(this);
    }
  }

  public void unsubscribe(Game game) {
    subscribedGames.remove(game);
    if (game.getSubscribers().contains(this)) {
      game.removeSubscriber(this);
    }
  }

  public void unsubscribe(Developer developer) {
    subscribedDevelopers.remove(developer);
    if (developer.getSubscribers().contains(this)) {
      developer.removeSubscriber(this);
    }
  }
  
  public void addNotification(Notification notification) {
    this.notifications.add(notification);
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

  public Set<Game> getDeveloped() {
    return developed;
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

  public String getPfp() {
    return pfp;
  }

  public void setPfp(String pfp) {
    this.pfp = pfp;
  }

  public String getBanner() {
    return banner;
  }

  public void setBanner(String banner) {
    this.banner = banner;
  }

  public Set<User> getFriends() {
    return Collections.unmodifiableSet(friends);
  }

  public Set<User> getFriendRequestsSent() {
    return Collections.unmodifiableSet(friendRequestsSent);
  }

  public Set<User> getFriendRequestsPending() {
    return Collections.unmodifiableSet(friendRequestsPending);
  }
  
  public Set<Game> getSubscribedGames() {
    return Collections.unmodifiableSet(subscribedGames);
  }

  public Set<Developer> getSubscribedDevelopers() {
    return Collections.unmodifiableSet(subscribedDevelopers);
  }
  
  public void ban() {
    this.isBanned = true;
  }
  
  public void unban() {
    this.isBanned = false;
  }
  
  public boolean isBanned() {
    return isBanned;
  }
  
  public Set<Notification> getNotifications() {
    return Collections.unmodifiableSet(notifications);
  }

  // INTERNAL HELPERS //

  protected Set<User> getFriendsInternal() {
    return friends;
  }

  protected Set<User> getFriendRequestsSentInternal() {
    return friendRequestsSent;
  }

  protected Set<User> getFriendRequestsPendingInternal() {
    return friendRequestsPending;
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
