package services;

import values.Rol;
import model.User;

public class UserService {
    public static boolean isAbleToCreateGame(User user) {
        return user.getRol() == Rol.DEVELOPER || user.getRol() == Rol.ADMIN;
    }
    
    public static boolean canBan(User user) {
        return user.getRol() == Rol.ADMIN;
    }
}
