package repositories;

import model.Notification;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

public class NotificationRepository {
  
  private final EntityManager entityManager;
  
  public NotificationRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }
  
  public Optional<Notification> findById(Long id) {
    return Optional.ofNullable(entityManager.find(Notification.class, id));
  }
  
  public List<Notification> listAll() {
    return entityManager.createQuery("SELECT n FROM Notification n", Notification.class).getResultList();
  }
  
  public interfaces.Notification persist(interfaces.Notification notification) {
    EntityTransaction tx = entityManager.getTransaction();
    tx.begin();
    entityManager.persist(notification);
    tx.commit();
    return notification;
  }
}
