package services;

import values.Rol;
import model.User;

public class ShelfService {
  public static boolean canViewPrivateShelves(User owner, User user) {
    return user.getRol() == Rol.ADMIN
            || owner.getId().equals(user.getId())
            || owner.getFriends().contains(user);
  }
  
  public static boolean canDeleteShelf(User owner, User user) {
    return user.getRol() == Rol.ADMIN
        || owner.getId().equals(user.getId());
  }
}
