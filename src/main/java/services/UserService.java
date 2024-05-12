package services;

import model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

public class UserService {

    private final EntityManager entityManager;

    public UserService(EntityManager entityManager) {
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
        new ShelfService(entityManager).deleteAll(); // delete child
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

    public User persist(User user) {
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.persist(user);
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
