package repositories;

import model.Shelf;
import values.Rol;
import model.Developer;
import model.Game;
import model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.*;
import java.util.stream.Collectors;

public class UserRepository {

    private final EntityManager entityManager;

    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    public Optional<User> findByUsername(String username) {
        return entityManager
                .createQuery("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(:username)", User.class)
                .setParameter("username", username).getResultList()
                .stream()
                .findFirst();
    }
    
    public List<User> findByUsernameLike(String username) {
        return entityManager
            .createQuery("SELECT u FROM User u WHERE LOWER(u.username) LIKE :like", User.class)
            .setParameter("like", username.toLowerCase() + "%").getResultList();
    }

    public Optional<User> findByEmail(String email) {
        return entityManager
                .createQuery("SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(:email)", User.class)
                .setParameter("email", email).getResultList()
                .stream()
                .findFirst();
    }

    public List<User> listAll() {
        return entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    public void deleteUserByID(Long id) {
        Optional<User> user = findById(id);
        // if (user.isEmpty())
        EntityTransaction tx = entityManager.getTransaction();

        if (user.isEmpty()) {
            return;
        }

        ShelfRepository shelfRepository = new ShelfRepository(entityManager);
        List<Shelf> shelves = shelfRepository.listByUser(user.get());
        for (Shelf shelf : shelves) {
            shelfRepository.deleteShelf(shelf, user.get());
        }

        GameRepository gameRepository = new GameRepository(entityManager);
        List<Game> games = gameRepository.listByOwner(user.get());
        for (Game game : games) {
            gameRepository.delete(game.getId());
        }

        tx.begin();
    
        // Remove user from friends
        entityManager.createNativeQuery("DELETE FROM user_friends WHERE user_id = :id OR friend_id = :id")
            .setParameter("id", id)
            .executeUpdate();
    
        // Remove user from friend requests sent
        entityManager.createNativeQuery("DELETE FROM friend_requests_sent WHERE user_id = :id OR request_sent_to_id = :id")
            .setParameter("id", id)
            .executeUpdate();
    
        // Remove user from friend requests pending
        entityManager.createNativeQuery("DELETE FROM friend_requests_pending WHERE user_id = :id OR request_pending_from_id = :id")
            .setParameter("id", id)
            .executeUpdate();
    
        // Remove user's liked reviews
        entityManager.createNativeQuery("DELETE FROM liked_by WHERE user_id = :id")
            .setParameter("id", id)
            .executeUpdate();
    
        // Remove user's disliked reviews
        entityManager.createNativeQuery("DELETE FROM disliked_by WHERE user_id = :id")
            .setParameter("id", id)
            .executeUpdate();
    
        // Remove user's upvoted games
        entityManager.createNativeQuery("DELETE FROM games_upvoted WHERE user_id = :id")
            .setParameter("id", id)
            .executeUpdate();
    
        // Remove user from subscribed games
        entityManager.createNativeQuery("DELETE FROM game_subscriptions WHERE user_id = :id")
            .setParameter("id", id)
            .executeUpdate();
    
        // Remove user from subscribed developers
        entityManager.createNativeQuery("DELETE FROM developer_subscriptions WHERE user_id = :id OR developer_id = :id")
            .setParameter("id", id)
            .executeUpdate();
    
        // Delete notifications owned by user
        entityManager.createNativeQuery("DELETE FROM notification WHERE owner_id = :id")
            .setParameter("id", id)
            .executeUpdate();
    
        // Delete reviews authored by user
        entityManager.createNativeQuery("DELETE FROM review WHERE author_id = :id")
            .setParameter("id", id)
            .executeUpdate();

        // Delete developer
        entityManager.createNativeQuery("DELETE FROM developer WHERE user_id = :id")
            .setParameter("id", id)
            .executeUpdate();
    
        // Delete the user itself
        entityManager.createNativeQuery("DELETE FROM user WHERE id = :id")
            .setParameter("id", id)
            .executeUpdate();
        
        tx.commit();
    }

    public void subscribe(User user, Game game) {
        user.subscribe(game);
        persist(user);
    }

    public void subscribe(User user, Developer dev) {
        user.subscribe(dev);
        persist(user);
    }

    public void unsubscribe(User user, Game game) {
        user.unsubscribe(game);
        persist(user);
    }

    public void unsubscribe(User user, Developer dev) {
        user.unsubscribe(dev);
        persist(user);
    }
    
    public void ban(User user) {
        user.ban();
        persist(user);
    }
    
    public void unban(User user) {
        user.unban();
        persist(user);
    }

    public User persist(User user) {
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.persist(user);
        if (user.getRol() == Rol.DEVELOPER) {
            entityManager.persist(new Developer(user));
        }
        tx.commit();
        return user;
    }

    public boolean usernameInUse(User user) {
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        if (findByUsername(user.getUsername()).isPresent()) {
            tx.commit();
            return true;
        }
        tx.commit();
        return false;
    }

    public boolean emailInUse(User user) {
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        if (findByEmail(user.getEmail()).isPresent()) {
            tx.commit();
            return true;
        }
        tx.commit();
        return false;
    }
}
