package repositories;

import values.ErrorMessages;
import values.Rol;
import values.responses.ErrorResponse;
import values.responses.GameResponse;
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
                .createQuery("SELECT g FROM Game g WHERE LOWER(g.name) LIKE LOWER(:name)", Game.class)
                .setParameter("name", name).getResultList()
                .stream()
                .findFirst();
    }
    
    public List<Game> findByNameLike(String name) {
        return entityManager
            .createQuery("SELECT g FROM Game g WHERE LOWER(g.name) LIKE :like", Game.class)
            .setParameter("like", name.toLowerCase() + "%").getResultList();
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
    
        // Delete related entities
        entityManager.createNativeQuery("DELETE FROM game_in_shelf gis WHERE gis.game_id = :id")
            .setParameter("id", id)
            .executeUpdate();
    
        entityManager.createNativeQuery("DELETE FROM games_tagged gt WHERE gt.game_id = :id")
            .setParameter("id", id)
            .executeUpdate();
    
        entityManager.createNativeQuery("DELETE FROM games_upvoted gu WHERE gu.game_id = :id")
            .setParameter("id", id)
            .executeUpdate();
    
        entityManager.createNativeQuery("DELETE FROM game_subscriptions gs WHERE gs.game_id = :id")
            .setParameter("id", id)
            .executeUpdate();
    
        entityManager.createNativeQuery("DELETE FROM upvoted_by ub WHERE ub.game_id = :id")
            .setParameter("id", id)
            .executeUpdate();
    
        entityManager.createNativeQuery("DELETE FROM News n WHERE n.game_id = :id")
            .setParameter("id", id)
            .executeUpdate();
    
        entityManager.createNativeQuery("DELETE FROM Review r WHERE r.game_id = :id")
            .setParameter("id", id)
            .executeUpdate();
    
        // Delete the game itself
        entityManager.createNativeQuery("DELETE FROM Game g WHERE g.id = :id")
            .setParameter("id", id)
            .executeUpdate();

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
