package repositories;

import model.Game;
import model.Shelf;
import model.User;
import values.ErrorMessages;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ShelfRepository {

    private final EntityManager entityManager;

    public ShelfRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<Shelf> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Shelf.class, id));
    }

    public Optional<Shelf> findByName(String name) {
        return entityManager
                .createQuery("SELECT s FROM Shelf s WHERE LOWER(s.name) LIKE LOWER(:name)", Shelf.class)
                .setParameter("name", name).getResultList()
                .stream()
                .findFirst();
    }

    public List<Shelf> listByUser(User user) {
        return entityManager
                .createQuery("SELECT s FROM Shelf s JOIN User u ON s.owner = u WHERE LOWER(u.username) LIKE LOWER(:username)", Shelf.class)
                .setParameter("username", user.getUsername()).getResultList();
    }

    public Optional<Shelf> find(String name, User user) {
        List<Shelf> shelves = listByUser(user);
        Shelf res = null;
        for(Shelf s : shelves) {
            if(s.getName().equals(name)) {
                res = s;
            }
        }
        return Optional.ofNullable(res);
    }

    public List<Shelf> listAll() {
        return entityManager.createQuery("SELECT s FROM Shelf s", Shelf.class).getResultList();
    }

    public void deleteAll() {
        entityManager.createQuery("DELETE FROM Shelf").executeUpdate();
    }

    public void addGame(Shelf shelf, User user, Game game) {
        EntityTransaction t = entityManager.getTransaction();

        t.begin();

        Optional<Shelf> managedShelf = find(shelf.getName(), user);
        if (managedShelf.isEmpty()) {
            throw new NoSuchElementException(ErrorMessages.informationNotFound("Shelf"));
        }
        Optional<Game> managedGame = new GameRepository(entityManager).findByName(game.getName());
        if(managedGame.isEmpty()) {
            throw new NoSuchElementException(ErrorMessages.informationNotFound("Game"));
        }

        managedShelf.get().addGame(managedGame.get());

        persist(managedShelf.get());

        t.commit();
    }
    
    public void takeOutGame(Shelf shelf, User user, Game game) {
        EntityTransaction t = entityManager.getTransaction();
        
        t.begin();
    
        Optional<Shelf> managedShelf = find(shelf.getName(), user);
        if (managedShelf.isEmpty()) {
            throw new NoSuchElementException(ErrorMessages.informationNotFound("Shelf"));
        }
        Optional<Game> managedGame = new GameRepository(entityManager).findByName(game.getName());
        if(managedGame.isEmpty()) {
            throw new NoSuchElementException(ErrorMessages.informationNotFound("Game"));
        }
    
        managedShelf.get().takeOutGame(game);
        
        persist(managedShelf.get());
        
        t.commit();
    }
    
    public void deleteShelf(Shelf shelf, User user) {
        EntityTransaction t = entityManager.getTransaction();
    
        t.begin();
    
        Optional<Shelf> managedShelf = find(shelf.getName(), user);
        if (managedShelf.isEmpty()) {
            throw new NoSuchElementException(ErrorMessages.informationNotFound("Shelf"));
        }
        
        long id = managedShelf.get().getId();
    
        entityManager.createNativeQuery("DELETE FROM game_in_shelf WHERE shelf_id = :shelfId")
            .setParameter("shelfId", id)
            .executeUpdate();
    
        entityManager.createNativeQuery("DELETE FROM shelf WHERE id = :shelfId")
            .setParameter("shelfId", id)
            .executeUpdate();
    
        t.commit();
    }

    public Shelf persist(Shelf shelf) {
        entityManager.persist(shelf);
        return shelf;
    }
}
