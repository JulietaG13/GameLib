package repositories;

import entities.TagType;
import model.Tag;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

public class TagRepository {
  
  private final EntityManager entityManager;
  
  public TagRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }
  
  public Optional<Tag> findById(Long id) {
    return Optional.ofNullable(entityManager.find(Tag.class, id));
  }
  
  public Optional<Tag> findByName(String name) {
    return entityManager
        .createQuery("SELECT t FROM Tag t WHERE t.name LIKE :name", Tag.class)
        .setParameter("name", name).getResultList()
        .stream()
        .findFirst();
  }
  
  public List<Tag> listAll() {
    return entityManager.createQuery("SELECT t FROM Tag t", Tag.class).getResultList();
  }
  
  public List<Tag> listAllType(TagType tagType) {
    return entityManager.createQuery("SELECT t FROM Tag t WHERE t.tagType = :tagType", Tag.class)
        .setParameter("tagType", tagType)
        .getResultList();
  }
  
  public Tag persist(Tag tag) {
    EntityTransaction tx = entityManager.getTransaction();
    tx.begin();
    entityManager.persist(tag);
    tx.commit();
    return tag;
  }
}
