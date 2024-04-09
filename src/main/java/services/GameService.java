package services;

import model.Game;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class GameService {

    private final EntityManager entityManager;

    public GameService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<Game> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Game.class, id));
    }

    public Optional<Game> findByTitle(String title) {
        return entityManager
                .createQuery("SELECT g FROM Game g WHERE g.title LIKE :title", Game.class)
                .setParameter("title", title).getResultList()
                .stream()
                .findFirst();
    }

    public List<Game> listAll() {
        return entityManager.createQuery("SELECT g FROM Game g", Game.class).getResultList();
    }

    public void deleteAll() {
        entityManager.createQuery("DELETE FROM Game").executeUpdate();
    }

    public Game persist(Game game) {
        entityManager.persist(game);
        return game;
    }
}
