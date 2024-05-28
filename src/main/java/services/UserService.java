package services;

import entities.Rol;
import model.User;

public class UserService {
    public static boolean isAbleToCreateGame(User user) {
        return user.getRol() == Rol.DEVELOPER || user.getRol() == Rol.ADMIN;
    }
}
