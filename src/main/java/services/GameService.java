package services;

import entities.responses.GameResponse;
import model.Game;
import model.Tag;

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
    
    public List<Game> listByLatest(int max) {
        return entityManager.createQuery("SELECT g FROM Game g ORDER BY g.lastUpdate DESC", Game.class)
            .setMaxResults(max)
            .getResultList();
    }

    public void deleteAll() {
        entityManager.createQuery("DELETE FROM Game").executeUpdate();
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

        if (!game.getReleaseDate().equals(gameUpdate.getReleaseDate())) {
            game.setReleaseDate(gameUpdate.getReleaseDate(), lastUpdate);
        }

        if (!game.getLastUpdate().equals(gameUpdate.getLastUpdate())) {
            game.setLastUpdate(gameUpdate.getLastUpdate());
        }
        //TODO(rest of the updates)
        
        persist(game);
        
        return new GameResponse(false, game, "OK!");
    }
    
    public GameResponse addTag(Game game, Tag tag) {
        EntityTransaction tx = entityManager.getTransaction();
        
        tx.begin();
        Optional<Game> managedGame = findById(game.getId());
        Optional<Tag> managedTag = new TagService(entityManager).findById(tag.getId());
        if (managedGame.isEmpty()) {
            return new GameResponse(true, "Theres no game with id " + game.getId() + "!");
        }
        if (managedTag.isEmpty()) {
            return new GameResponse(true, "Theres no tag with id " + tag.getId() + "!");
        }
        tx.commit();
        
        managedGame.get().addTag(managedTag.get());
        persist(managedGame.get());
        
        return new GameResponse(false, managedGame.get(), "OK!");
    }
    
    public Game persist(Game game) {
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.persist(game);
        tx.commit();
        return game;
    }
}
