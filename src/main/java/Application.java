import com.google.common.base.Strings;
import com.google.gson.Gson;
import model.User;
import persistence.Database;
import repository.Users;
import spark.Spark;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Arrays;
import java.util.List;

public class Application {

    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        new Database().startDBServer();
        final EntityManagerFactory factory = Persistence.createEntityManagerFactory("gamelib");
        final EntityManager entityManager = factory.createEntityManager();

        Spark.port(4567);

        storeUsers1(entityManager);

        Spark.options("/*", (req, res) -> {
            String accessControlRequestHeaders = req.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                res.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = req.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                res.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        Spark.before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "*");
            res.type("application/json");
        });

        Spark.get("/users", "application/json", (req, resp) -> {

            resp.type("application/json");
            resp.status(201);

            Users users = new Users(entityManager);

            return gson.toJson(users.listAll());
        });
    }

    private static void storeUsers1(EntityManager entityManager) {
        final EntityTransaction transaction = entityManager.getTransaction();
        Users users = new Users(entityManager);

        transaction.begin();
        if(users.listAll().isEmpty()) {
            for(int i = 1; i < 5; i++) {
                User u = User.create("username" + i).email("user" + i + "@mail.com").password("qwerty123").build();
                users.persist(u);
            }
        }
        transaction.commit();
    }
}

