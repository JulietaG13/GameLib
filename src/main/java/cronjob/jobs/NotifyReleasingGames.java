package cronjob.jobs;

import model.Game;
import model.News;
import model.User;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import repositories.GameRepository;
import repositories.NewsRepository;
import repositories.UserRepository;
import services.NewsService;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class NotifyReleasingGames implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        EntityManagerFactory factory = (EntityManagerFactory) context.getJobDetail().getJobDataMap().get("entityManagerFactory");
        EntityManager entityManager = factory.createEntityManager();

        // Get realising games.
        LocalDate today = LocalDate.now();
        List<Game> games = getTodayGames(entityManager, today);

        if (games.isEmpty()) {
            return;
        }

        // Notify release from each game to users
        games.forEach(game -> {
            User author = game.getOwner();
            if (author == null) {
                return;
            }
            News releasingNew = createReleasingNews(game, author);
            NewsService.notifyUsers(releasingNew, entityManager);
        });

        entityManager.close();
    }

    private List<Game> getTodayGames(EntityManager entityManager, LocalDate today) {
        GameRepository gameRepository = new GameRepository(entityManager);
        return gameRepository.findByDate(today);
    }

    private News createReleasingNews(Game game, User bot) {
        String title = "New game released!";
        String description = "The game " + game.getName() + " is releasing today! Don't miss it!";
        News news = new News(title, description, game, bot);

        return news;
    }
}
