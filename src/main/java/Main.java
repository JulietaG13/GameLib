import model.User;
import persistence.Database;
import repository.Users;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {
        new Database().startDBServer();
        final EntityManagerFactory factory = Persistence.createEntityManagerFactory("gamelib");

        final EntityManager entityManager = factory.createEntityManager();

        //sample1(entityManager);

        List<User> users = new Users(entityManager).listAll();
        StringBuilder str = new StringBuilder();
        for(User u : users) str.append(u.getUsername()).append(" - ").append(u.getEmail()).append("<br>");

        // http://localhost:4567/hello
        get("/hello", (req, res) -> str.toString());

        entityManager.close();
    }

    /*
    private static void sample1(EntityManager entityManager) {
        final EntityTransaction transaction = entityManager.getTransaction();
        Users usersRepo = new Users(entityManager);

        transaction.begin();
        final User user1 = new User("username1", "u1@jedis.org", "qwerty123");
        final User user2 = new User("username2", "u2@jedis.org", "qwerty123");
        final User user3 = new User("username3", "u3@jedis.org", "qwerty123");
        final User user4 = new User("username4", "u4@jedis.org", "qwerty123");

        usersRepo.persist(user1);
        usersRepo.persist(user2);
        usersRepo.persist(user3);
        usersRepo.persist(user4);

        transaction.commit();
    }
    */

}
