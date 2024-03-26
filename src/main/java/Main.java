import model.Publication;
import model.Rol;
import model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        final EntityManagerFactory factory = Persistence.createEntityManagerFactory("gamelib");

        final EntityManager entityManager = factory.createEntityManager();

        sample1(entityManager);

        entityManager.close();
    }

    private static void sample1(EntityManager entityManager) {
        final EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();
        final User user1 = new User("username1", "u1@jedis.org", "12345678");
        final User user2 = new User("username2", "u2@jedis.org", "qwerty123");

        entityManager.persist(user1);
        entityManager.persist(user2);

        transaction.commit();
    }

}
