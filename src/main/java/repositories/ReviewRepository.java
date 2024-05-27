package repositories;

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

public class ReviewRepository {
  
  private final EntityManager entityManager;
  
  public ReviewRepository(EntityManager entityManager) {
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
    UserRepository userRepository = new UserRepository(entityManager);
    GameRepository gameRepository = new GameRepository(entityManager);
    EntityTransaction tx = entityManager.getTransaction();
    
    tx.begin();
    Optional<User> managedUser = userRepository.findById(author.getId());
    Optional<Game> managedGame = gameRepository.findById(game.getId());
    if (managedGame.isEmpty()) {
      return new ErrorResponse(404, "Theres no game with id " + game.getId() + "!");
    }
    if (managedUser.isEmpty()) {
      return new ErrorResponse(404, "Theres no user with id " + author.getId() + "!");
    }
    tx.commit();
  
    managedUser.get().addReview(review);
    managedGame.get().addReview(review);
  
    userRepository.persist(managedUser.get());
    gameRepository.persist(managedGame.get());
    return new ReviewResponse(review);
  }
  
  public Responses likeReview(Review review, User user) {
    UserRepository userRepository = new UserRepository(entityManager);
    EntityTransaction tx = entityManager.getTransaction();
    
    tx.begin();
    Optional<Review> managedReview = findById(review.getId());
    Optional<User> managedUser = userRepository.findById(user.getId());
    if (managedUser.isEmpty()) {
      return new ErrorResponse(404, "Theres no user with id " + user.getId() + "!");
    }
    if (managedReview.isEmpty()) {
      return new ErrorResponse(404, "Theres no review with id " + review.getId() + "!");
    }
    tx.commit();
    
    managedUser.get().likeReview(review);
    userRepository.persist(managedUser.get());
    
    return new ReviewResponse(review);
  }
  
  public Responses dislikeReview(Review review, User user) {
    UserRepository userRepository = new UserRepository(entityManager);
    EntityTransaction tx = entityManager.getTransaction();
    
    tx.begin();
    Optional<Review> managedReview = findById(review.getId());
    Optional<User> managedUser = userRepository.findById(user.getId());
    if (managedUser.isEmpty()) {
      return new ErrorResponse(404, "Theres no user with id " + user.getId() + "!");
    }
    if (managedReview.isEmpty()) {
      return new ErrorResponse(404, "Theres no review with id " + review.getId() + "!");
    }
    tx.commit();
    
    managedUser.get().dislikeReview(review);
    userRepository.persist(managedUser.get());
    
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
