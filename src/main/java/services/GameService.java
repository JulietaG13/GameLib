package services;

import entities.Rol;
import entities.responses.ErrorResponse;
import entities.responses.GameResponse;
import interfaces.Responses;
import model.Game;
import model.Tag;
import model.User;

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

    public Optional<Game> findByName(String name) {
        return entityManager
                .createQuery("SELECT g FROM Game g WHERE g.name LIKE :name", Game.class)
                .setParameter("name", name).getResultList()
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

    public void delete(Long id) {
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        Game game = entityManager.find(Game.class, id);

        System.out.println(100);
        // Removes reviews related to the game
        entityManager.createQuery("DELETE FROM Review r WHERE r.game.id = :id")
            .setParameter("id", id)
            .executeUpdate();

        System.out.println(101);
        // Removes shelves related to the game
        game.getInShelves().forEach(shelf -> shelf.getGames().remove(game));

        System.out.println(102);
        // Removes tags related to the game
        game.getTags().forEach(tag -> tag.getTaggedGames().remove(game));

        System.out.println(103);
        // Finally, removes the game
        entityManager.createQuery("DELETE FROM Game g WHERE g.id = :id")
            .setParameter("id", id)
            .executeUpdate();
        System.out.println(104);

        tx.commit();
    }

    public void deleteAll() {
        entityManager.createQuery("DELETE FROM Game").executeUpdate();
    }
    
    public Responses update(User user, Long id, Game gameUpdate, LocalDateTime lastUpdate) {
        EntityTransaction tx = entityManager.getTransaction();
        
        tx.begin();
        Optional<Game> managedGame = findById(id);
        if (managedGame.isEmpty()) {
            return new ErrorResponse(404, "Theres no game with id " + id + "!");
        }
        tx.commit();
        Game game = managedGame.get();

        if (!isUserAllowedToManageGame(user, game)) {
            return new ErrorResponse(403, "You are not allowed to change the game!");
        }

        if (gameUpdate.getGamePicture() != null && !game.getGamePicture().equals(gameUpdate.getGamePicture())) {
            game.setGamePicture(gameUpdate.getGamePicture(), lastUpdate);
        }
        
        if (gameUpdate.getName() != null && !game.getName().equals(gameUpdate.getName())) {
            game.setName(gameUpdate.getName(), lastUpdate);
        }
        
        if (gameUpdate.getDescription() != null && !game.getDescription().equals(gameUpdate.getDescription())) {
            game.setDescription(gameUpdate.getDescription(), lastUpdate);
        }

        if (gameUpdate.getReleaseDate() != null && !game.getReleaseDate().equals(gameUpdate.getReleaseDate())) {
            game.setReleaseDate(gameUpdate.getReleaseDate(), lastUpdate);
        }
        
        //TODO(rest of the updates)
        
        persist(game);
        
        return new GameResponse(game);
    }
    
    public Responses addTag(User user, Game game, Tag tag) {
        EntityTransaction tx = entityManager.getTransaction();
        
        tx.begin();
        Optional<Game> managedGame = findById(game.getId());
        Optional<Tag> managedTag = new TagService(entityManager).findById(tag.getId());
        if (managedGame.isEmpty()) {
            return new ErrorResponse(404, "Theres no game with id " + game.getId() + "!");
        }
        if (managedTag.isEmpty()) {
            return new ErrorResponse(404, "Theres no tag with id " + tag.getId() + "!");
        }
        tx.commit();

        if (!isUserAllowedToManageGame(user, managedGame.get())) {
            return new ErrorResponse(403, "You are not allowed to change the game!");
        }
        
        managedGame.get().addTag(managedTag.get());
        persist(managedGame.get());
        
        return new GameResponse(managedGame.get());
    }

    private boolean isUserAllowedToManageGame(User user, Game game) {
        if (user.getRol() == Rol.ADMIN) return true;
        return user.getRol() == Rol.DEVELOPER && user.equals(game.getOwner());
    }

    public boolean isUserAllowed(User user) {
        return user.getRol() != Rol.USER;
    }

    public Game persist(Game game) {
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.persist(game);
        tx.commit();
        return game;
    }
}
