package services;

import values.Rol;
import model.Game;
import model.User;

public class GameService {

    public static boolean isAbleToEditGame(User user, Game game) {
        return user.getRol() == Rol.ADMIN || user.getId().equals(game.getOwner().getId());
    }
}
