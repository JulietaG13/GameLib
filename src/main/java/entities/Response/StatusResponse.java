package entities.Response;

import interfaces.Response;

public class StatusResponse implements Response {
    private final boolean hasError;
    private final String message;
    public final int statusCode;

    public StatusResponse(boolean hasError, int statusCode, String message) {
        this.hasError = hasError;
        this.statusCode = statusCode;
        this.message = message;
    }

    public StatusResponse(boolean hasError, int statusCode) {
        this.hasError = hasError;
        this.statusCode = statusCode;
        if (hasError) {
            this.message = "Error.";
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
