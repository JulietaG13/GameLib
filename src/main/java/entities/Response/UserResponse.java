package entities.Response;

import model.User;

public class UserResponse extends StatusResponse {
  private final User user;
  
  public UserResponse(boolean hasError, User user, int statusCode, String message) {
    super(hasError, statusCode, message);
    this.user = user;
  }
  
  public UserResponse(boolean hasError, User user, int statusCode) {
    super(hasError, statusCode);
    this.user = user;
  }
  
  public User getUser() {
    return user;
  }
}
