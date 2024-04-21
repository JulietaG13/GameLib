package entities.responses;

import interfaces.Response;

public class MessageResponse implements Response {
  private final boolean hasError;
  private final String message;
  
  public MessageResponse(boolean hasError, String message) {
    this.hasError = hasError;
    this.message = message;
  }
  
  public MessageResponse(boolean error) {
    this.hasError = error;
    if (hasError) {
      this.message = "Error";
    } else {
      this.message = "OK!";
    }
  }
  
  @Override
  public boolean hasError() {
    return hasError;
  }
  
  @Override
  public String getMessage() {
    return message;
  }
}
