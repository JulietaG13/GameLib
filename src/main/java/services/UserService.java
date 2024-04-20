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
                .createQuery("SELECT u FROM User u WHERE u.username LIKE :username", User.class)
                .setParameter("username", username).getResultList()
                .stream()
                .findFirst();
    }

    public Optional<User> findByEmail(String email) {
        return entityManager
                .createQuery("SELECT u FROM User u WHERE u.email LIKE :email", User.class)
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

    public User persist(User user) {
        entityManager.persist(user);
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
