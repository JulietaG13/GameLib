package repositories;

import model.Game;
import model.News;
import model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

public class NewsRepository {
  
  private final EntityManager entityManager;
  
  public NewsRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }
  
  public Optional<News> findById(Long id) {
    return Optional.ofNullable(entityManager.find(News.class, id));
  }
  
  public Optional<News> findByTitle(String title) {
    return entityManager
        .createQuery("SELECT n FROM News n WHERE n.title LIKE :title ORDER BY n.date DESC", News.class)
        .setParameter("title", title)
        .getResultList()
        .stream()
        .findFirst();
  }
  
  public List<News> findByGame(Game game, int max) {
    return entityManager
        .createQuery("SELECT n FROM News n WHERE n.game = :game ORDER BY n.date DESC", News.class)
        .setParameter("game", game)
        .setMaxResults(max)
        .getResultList();
  }
  
  public List<News> findByGame(Game game) {
    return entityManager
        .createQuery("SELECT n FROM News n WHERE n.game = :game ORDER BY n.date DESC", News.class)
        .setParameter("game", game)
        .getResultList();
  }
  
  public List<News> findByAuthor(User author, int max) {
    return entityManager
        .createQuery("SELECT n FROM News n WHERE n.author = :author ORDER BY n.date DESC", News.class)
        .setParameter("author", author)
        .setMaxResults(max)
        .getResultList();
  }
  
  public List<News> findByAuthor(User author) {
    return entityManager
        .createQuery("SELECT n FROM News n WHERE n.author = :author ORDER BY n.date DESC", News.class)
        .setParameter("author", author)
        .getResultList();
  }
  
  public List<News> listAll() {
    return entityManager.createQuery("SELECT n FROM News n ORDER BY n.date DESC", News.class).getResultList();
  }

  public boolean deleteById(Long id) {
    EntityTransaction tx = entityManager.getTransaction();
    tx.begin();

    Optional<News> newsOptional = findById(id);
    if (newsOptional.isPresent()) {
      entityManager.remove(newsOptional.get());
      tx.commit();
      return true;
    }

    tx.commit();
    return false;
  }
  
  public News persist(News news) {
    EntityTransaction tx = entityManager.getTransaction();
    tx.begin();
    entityManager.persist(news);
    tx.commit();
    return news;
  }
}
