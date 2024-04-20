package entities;

public class Response {
    public final boolean hasError;
    public final int statusCode;
    public final String message;

    public Response(boolean hasError, int statusCode, String message) {
        this.hasError = hasError;
        this.statusCode = statusCode;
        this.message = message;
    }

    public Response(boolean hasError, int statusCode) {
        this.hasError = hasError;
        this.statusCode = statusCode;
        if (hasError) {
            this.message = "Error.";
        } else {
            this.message = "OK!";
        }
    }
}
