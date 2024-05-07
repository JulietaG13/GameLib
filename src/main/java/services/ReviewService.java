package services;

import entities.responses.ErrorResponse;
import entities.responses.ReviewResponse;
import interfaces.Responses;
import model.Game;
import model.Review;
import model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

public class ReviewService {
  
  private final EntityManager entityManager;
  
  public ReviewService(EntityManager entityManager) {
    this.entityManager = entityManager;
  }
  
  public Optional<Review> findById(Long id) {
    return Optional.ofNullable(entityManager.find(Review.class, id));
  }
  
  public List<Review> listAll() {
    return entityManager.createQuery("SELECT r FROM Review r", Review.class).getResultList();
  }
  
  public List<Review> listByAuthor(User user) {
    return entityManager.createQuery("SELECT r FROM Review r WHERE r.author = :user", Review.class)
        .setParameter("user", user)
        .getResultList();
  }
  
  public List<Review> listByGame(Game game) {
    return entityManager.createQuery("SELECT r FROM Review r WHERE r.game = :game", Review.class)
        .setParameter("game", game)
        .getResultList();
  }
  
  public Responses addReview(Review review, User author, Game game) {
    UserService userService = new UserService(entityManager);
    GameService gameService = new GameService(entityManager);
    EntityTransaction tx = entityManager.getTransaction();
    
    tx.begin();
    Optional<User> managedUser = userService.findById(author.getId());
    Optional<Game> managedGame = gameService.findById(game.getId());
    if (managedGame.isEmpty()) {
      return new ErrorResponse(404, "Theres no game with id " + game.getId() + "!");
    }
    if (managedUser.isEmpty()) {
      return new ErrorResponse(404, "Theres no user with id " + author.getId() + "!");
    }
    tx.commit();
  
    managedUser.get().addReview(review);
    managedGame.get().addReview(review);
  
    userService.persist(managedUser.get());
    gameService.persist(managedGame.get());
    return new ReviewResponse(review);
  }
  
  public Responses likeReview(Review review, User user) {
    UserService userService = new UserService(entityManager);
    EntityTransaction tx = entityManager.getTransaction();
    
    tx.begin();
    Optional<Review> managedReview = findById(review.getId());
    Optional<User> managedUser = userService.findById(user.getId());
    if (managedUser.isEmpty()) {
      return new ErrorResponse(404, "Theres no user with id " + user.getId() + "!");
    }
    if (managedReview.isEmpty()) {
      return new ErrorResponse(404, "Theres no review with id " + review.getId() + "!");
    }
    tx.commit();
    
    managedUser.get().likeReview(review);
    userService.persist(managedUser.get());
    
    return new ReviewResponse(review);
  }
  
  public Responses dislikeReview(Review review, User user) {
    UserService userService = new UserService(entityManager);
    EntityTransaction tx = entityManager.getTransaction();
    
    tx.begin();
    Optional<Review> managedReview = findById(review.getId());
    Optional<User> managedUser = userService.findById(user.getId());
    if (managedUser.isEmpty()) {
      return new ErrorResponse(404, "Theres no user with id " + user.getId() + "!");
    }
    if (managedReview.isEmpty()) {
      return new ErrorResponse(404, "Theres no review with id " + review.getId() + "!");
    }
    tx.commit();
    
    managedUser.get().dislikeReview(review);
    userService.persist(managedUser.get());
    
    return new ReviewResponse(review);
  }
  
  public Review persist(Review review) {
    EntityTransaction tx = entityManager.getTransaction();
    tx.begin();
    entityManager.persist(review);
    tx.commit();
    return review;
  }
}
