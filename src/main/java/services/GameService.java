package services;

import entities.Response.GameResponse;
import model.Game;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.time.LocalDateTime;
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
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.persist(game);
        tx.commit();
        return game;
    }
    
    public GameResponse update(Long id, Game gameUpdate, LocalDateTime lastUpdate) {
        EntityTransaction tx = entityManager.getTransaction();
        
        tx.begin();
        Optional<Game> managedGame = findById(id);
        if (managedGame.isEmpty()) {
            return new GameResponse(true, "Theres no game with id " + id + "!");
        }
        tx.commit();
        Game game = managedGame.get();
        
        if (!game.getTitle().equals(gameUpdate.getTitle())) {
            game.setTitle(gameUpdate.getTitle(), lastUpdate);
        }
        
        if (!game.getDescription().equals(gameUpdate.getDescription())) {
            game.setDescription(gameUpdate.getDescription(), lastUpdate);
        }
        //TODO(rest of the updates)
        
        persist(game);
        
        return new GameResponse(false, game, "OK!");
    }
}
