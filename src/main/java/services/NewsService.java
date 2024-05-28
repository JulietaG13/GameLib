package services;

import entities.Rol;
import model.News;
import model.User;

public class NewsService {
    public static boolean isAbleToDeleteNews(User user, News news) {
        return user.getRol() == Rol.ADMIN || user.getId().equals(news.getAuthor().getId());
    }
}
