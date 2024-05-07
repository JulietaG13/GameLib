package entities.responses;

import interfaces.Responses;
import model.Review;

public class ReviewResponse implements Responses {
  private final Review review;
  
  public ReviewResponse(Review review) {
    this.review = review;
  }

  @Override
  public Review getReview() {
    return review;
  }
}
