package services;

import model.Game;
import model.Shelf;
import model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ShelfService {

    private final EntityManager entityManager;

    public ShelfService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<Shelf> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Shelf.class, id));
    }

    public Optional<Shelf> findByName(String name) {
        return entityManager
                .createQuery("SELECT s FROM Shelf s WHERE s.name LIKE :name", Shelf.class)
                .setParameter("name", name).getResultList()
                .stream()
                .findFirst();
    }

    public List<Shelf> listByUser(User user) {
        return entityManager
                .createQuery("SELECT s FROM Shelf s JOIN User u ON s.user = u WHERE u.username LIKE :username", Shelf.class)
                .setParameter("username", user.getUsername()).getResultList();
    }

    public Shelf find(String name, User user) {
        List<Shelf> shelves = listByUser(user);
        Shelf res = null;
        for(Shelf s : shelves) {
            if(s.getName().equals(name)) {
                res = s;
            }
        }
        return res;
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
        /*
        Optional<Shelf> managedShelf = findById(shelf.getId());
        if(managedShelf.isEmpty()) throw new NoSuchElementException("Shelf not in BD!");

        Optional<Game> managedGame = new GameService(entityManager).findById(game.getId());
        if(managedGame.isEmpty()) throw new NoSuchElementException("Game not in BD!");
        */

        Shelf managedShelf = find(shelf.getName(), user);
        Optional<Game> managedGame = new GameService(entityManager).findByName(game.getName());
        if(managedGame.isEmpty()) throw new NoSuchElementException("Game not in BD!");

        managedShelf.addGame(managedGame.get());

        persist(managedShelf);

        t.commit();
    }

    public Shelf persist(Shelf shelf) {
        entityManager.persist(shelf);
        return shelf;
    }
}
