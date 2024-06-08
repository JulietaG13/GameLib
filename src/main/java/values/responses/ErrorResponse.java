package values.responses;

import interfaces.Responses;

public class ErrorResponse implements Responses {
  private final int statusCode;
  private final String message;
  
  public ErrorResponse(int statusCode, String message) {
    this.statusCode = statusCode;
    this.message = message;
  }

  @Override
  public boolean hasError() {
    return true;
  }
  
  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public Integer getStatusCode() {
    return statusCode;
  }
}
