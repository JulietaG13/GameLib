package values.responses;

import interfaces.Responses;
import model.User;

public class UserResponse implements Responses {
  private final User user;
  
  public UserResponse(User user) {
    this.user = user;
  }

  @Override
  public User getUser() {
    return user;
  }

  public static String genericMessage() {
    return "\"\"\"Something went wrong\"\"\"";
  }
}
