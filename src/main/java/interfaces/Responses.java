package interfaces;

import model.Game;
import model.Review;
import model.User;

public interface Responses {

  default Responses getResponse() {
    return null;
  };

  default String getMessage() {
    return "OK!";
  }

  default boolean hasError() {
    return getResponse() != null && getResponse().hasError();
  }

  default Integer getStatusCode() {
    return getResponse() == null ? null : getResponse().getStatusCode();
  }

  default boolean hasStatusCode() {
    return getStatusCode() != null;
  }

  default User getUser() {
    return getResponse() == null ? null : getResponse().getUser();
  }

  default boolean hasUser() {
    return getUser() != null;
  }

  default Game getGame() {
    return getResponse() == null ? null : getResponse().getGame();
  }

  default boolean hasGame() {
    return getGame() != null;
  }

  default Review getReview() {
    return getResponse() == null ? null : getResponse().getReview();
  }

  default boolean hasReview() {
    return getReview() != null;
  }
}
