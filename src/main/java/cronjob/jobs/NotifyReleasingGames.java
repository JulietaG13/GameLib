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

        // Get bot user
        User bot = getBot(entityManager);

        // Notify release from each game to users
        NewsRepository newsRepository = new NewsRepository(entityManager);
        games.forEach(game -> {
            News releasingNew = createReleasingNews(game, bot, newsRepository);
            NewsService.notifyUsers(releasingNew, entityManager);
        });

        entityManager.close();
    }

    private List<Game> getTodayGames(EntityManager entityManager, LocalDate today) {
        GameRepository gameRepository = new GameRepository(entityManager);
        return gameRepository.findByDate(today);
    }

    private User getBot(EntityManager entityManager) {
        String username = "bot";
        UserRepository userRepository = new UserRepository(entityManager);
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new RuntimeException("Bot user not found");
        }

        return user.get();
    }

    private News createReleasingNews(Game game, User bot, NewsRepository newsRepository) {
        String title = "New game released!";
        String description = "The game " + game.getName() + " is releasing today! Don't miss it!";
        News news = new News(title, description, game, bot);

        newsRepository.persist(news);

        return news;
    }
}
