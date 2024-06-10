package services;

import interfaces.Notification;
import model.*;
import repositories.DeveloperRepository;
import repositories.NotificationRepository;
import values.Rol;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class NewsService {
    
    public static boolean isAbleToDeleteNews(User user, News news) {
        return user.getRol() == Rol.ADMIN || user.getId().equals(news.getAuthor().getId());
    }
    
    public static boolean isAbleToAddNews(User user, Game game) {
        return user.getRol() == Rol.ADMIN || game.getOwner().getId().equals(user.getId());
    }
    
    public static void notifyUsers(News news, EntityManager em) {
        User author = news.getAuthor();
        Game game = news.getGame();
        
        Set<User> gameSubs = new HashSet<>(game.getSubscribers());
    
        Set<User> devSubs = new HashSet<>();
        if (author.getRol() == Rol.DEVELOPER) {
            Optional<Developer> dev = new DeveloperRepository(em).findByUserId(author.getId());
            if (dev.isEmpty()) {
                throw new RuntimeException("Somehow a User with Rol=DEVELOPER does not have a Developer instance associated");
            }
            devSubs = dev.get().getSubscribers();
        }
        gameSubs.addAll(devSubs);
        Set<User> subs = gameSubs;
        
        if (!subs.isEmpty()) {
            EmailService.sendMail(EmailService.getEmails(subs), news);
        }
    
        NotificationRepository notificationRepository = new NotificationRepository(em);
        subs.forEach(s -> {
            Notification notification = new GameNotification(
                s,
                "News from " + news.getGame().getName() + "! \nWritten by " + news.getAuthor().getUsername(),
                news.getGame()
            );
            notificationRepository.persist(notification);
        });
    }
}
