package services;

import entities.Rol;
import model.Game;
import model.News;
import model.User;

import java.util.Set;

public class NewsService {
    
    public static boolean isAbleToDeleteNews(User user, News news) {
        return user.getRol() == Rol.ADMIN || user.getId().equals(news.getAuthor().getId());
    }
    
    public static boolean isAbleToAddNews(User user, Game game) {
        return user.getRol() == Rol.ADMIN || game.getOwner().getId().equals(user.getId());
    }
    
    public static void notifyUsers(News news) {
        //User dev = news.getAuthor();  TODO()
        Game game = news.getGame();
    
        Set<User> gameSubs = game.getSubscribers();
        if (!gameSubs.isEmpty()) {
            EmailService.sendMail(EmailService.getEmails(gameSubs), news);
        }
    }
}
