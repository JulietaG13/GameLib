package repositories;

import values.Rol;
import model.Developer;
import model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

public class DeveloperRepository {

  private final EntityManager entityManager;

  public DeveloperRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }
  
  public Optional<Developer> findById(Long id) {
    return Optional.ofNullable(entityManager.find(Developer.class, id));
  }
  
  public Optional<Developer> findByUserId(Long userId) {
    Optional<Developer> dev = entityManager
        .createQuery("SELECT d FROM Developer d WHERE d.user.id = :user_id", Developer.class)
        .setParameter("user_id", userId).getResultList()
        .stream()
        .findFirst();
    
    if (dev.isPresent()) {
      return dev;
    }
    
    UserRepository userRepository = new UserRepository(entityManager);
    Optional<User> user = userRepository.findById(userId);
    if (user.isPresent() && user.get().getRol() == Rol.DEVELOPER) {
      Developer developer = new Developer(user.get());
      persist(developer);
      return Optional.of(developer);
    }
    
    return Optional.empty();
  }
  
  public Optional<Developer> findByUsername(String username) {
    return entityManager
        .createQuery("SELECT d FROM Developer d WHERE d.user.username LIKE :username", Developer.class)
        .setParameter("username", username).getResultList()
        .stream()
        .findFirst();
  }
  
  public List<Developer> listAll() {
    return entityManager.createQuery("SELECT d FROM Developer d", Developer.class).getResultList();
  }
  
  public Developer setupDonations(User dev, String publicKey, String accessToken) {
    Optional<Developer> developer = findByUserId(dev.getId());
    developer.get().setMpPublicKey(publicKey);
    developer.get().setMpAccessToken(accessToken);
    developer.get().setDonationsSetup(true);
    persist(developer.get());
    return developer.get();
  }
  
  public Developer removeDonations(User dev) {
    Optional<Developer> developer = findByUserId(dev.getId());
    developer.get().setMpPublicKey("");
    developer.get().setMpAccessToken("");
    developer.get().setDonationsSetup(false);
    persist(developer.get());
    return developer.get();
  }

  public Developer persist(Developer dev) {
    EntityTransaction tx = entityManager.getTransaction();
    tx.begin();
    entityManager.persist(dev);
    tx.commit();
    return dev;
  }
}
