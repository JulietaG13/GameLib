package repositories;

import entities.ErrorMessages;
import entities.Rol;
import entities.responses.ErrorResponse;
import entities.responses.GameResponse;
import interfaces.Responses;
import model.Game;
import model.Tag;
import model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class GameRepository {

    private final EntityManager entityManager;

    public GameRepository(EntityManager entityManager) {
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
    
    public Responses update(User user, Long id, Game gameUpdate, LocalDate lastUpdate) {
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

        String newCover = gameUpdate.getCover();
        String actualCover = game.getCover();
        if (newCover != null && !actualCover.equals(newCover)) {
            game.setCover(newCover, lastUpdate);
        }

        String newName = gameUpdate.getName();
        String actualName = game.getName();
        if (newName != null && !actualName.equals(newName)) {
            game.setName(newName, lastUpdate);
        }

        String newDesc = gameUpdate.getDescription();
        String actualDesc = game.getDescription();
        if (newDesc != null && !actualDesc.equals(newDesc)) {
            game.setDescription(newDesc, lastUpdate);
        }

        LocalDate newRelease = gameUpdate.getReleaseDate();
        LocalDate actualRelease = game.getReleaseDate();
        if (newRelease != null && !actualRelease.equals(newRelease)) {
            game.setReleaseDate(newRelease, lastUpdate);
        }
        
        //TODO(rest of the updates)
        
        persist(game);
        
        return new GameResponse(game);
    }
    
    public Responses addTag(User user, Game game, Tag tag) {
        EntityTransaction tx = entityManager.getTransaction();
        
        tx.begin();
        Optional<Game> managedGame = findById(game.getId());
        Optional<Tag> managedTag = new TagRepository(entityManager).findById(tag.getId());
        if (managedGame.isEmpty()) {
            return new ErrorResponse(404, ErrorMessages.informationNotFound("Game"));
        }
        if (managedTag.isEmpty()) {
            return new ErrorResponse(404, ErrorMessages.informationNotFound("Tag"));
        }
        tx.commit();

        if (!isUserAllowedToManageGame(user, managedGame.get())) {
            return new ErrorResponse(403, ErrorMessages.userNotAllowedToPerformAction());
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
