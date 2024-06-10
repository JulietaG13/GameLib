package repositories;

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

    public void deleteAll() {
        new ShelfRepository(entityManager).deleteAll(); // delete child
        entityManager.createQuery("DELETE FROM User").executeUpdate();
    }

    public void deleteUserByID(Long id) {
        Optional<User> user = findById(id);
        // if (user.isEmpty())
        EntityTransaction tx = entityManager.getTransaction();

        tx.begin();
        entityManager.createQuery("DELETE FROM Review R WHERE R.author = :user")
                .setParameter("user", user.get())
                .executeUpdate();

        entityManager.createQuery("DELETE FROM User U WHERE U.id = :id")
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
