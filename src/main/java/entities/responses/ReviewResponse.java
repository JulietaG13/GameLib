package entities.responses;

import interfaces.Response;
import model.Review;

public class ReviewResponse implements Response {
  private final boolean hasError;
  private final Review review;
  private final String message;
  
  public ReviewResponse(boolean hasError, Review review, String message) {
    this.hasError = hasError;
    this.review = review;
    this.message = message;
  }
  
  public ReviewResponse(boolean hasError, String message) {
    this.hasError = hasError;
    this.message = message;
    this.review = null;
  }
  
  @Override
  public boolean hasError() {
    return hasError;
  }
  
  @Override
  public String getMessage() {
    return message;
  }
  
  public Review getReview() {
    return review;
  }
}
