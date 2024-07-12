package repositories;

import model.DonationNotification;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

public class DonationNotificationRepository {
  
  private final EntityManager entityManager;
  
  public DonationNotificationRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }
  
  public Optional<DonationNotification> findById(Long id) {
    return Optional.ofNullable(entityManager.find(DonationNotification.class, id));
  }
  
  public Optional<DonationNotification> findByPreferenceId(String preferenceId) {
    return entityManager.createQuery("SELECT n FROM DonationNotification n WHERE n.preferenceId LIKE :preferenceId", DonationNotification.class)
        .setParameter("preferenceId", preferenceId)
        .getResultList()
        .stream()
        .findFirst();
  }
  
  public List<DonationNotification> listAll() {
    return entityManager.createQuery("SELECT n FROM DonationNotification n", DonationNotification.class).getResultList();
  }
  
  public interfaces.Notification persist(DonationNotification notification) {
    EntityTransaction tx = entityManager.getTransaction();
    tx.begin();
    entityManager.persist(notification);
    tx.commit();
    return notification;
  }
}
