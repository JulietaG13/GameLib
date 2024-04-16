package entities;

public class ResponsePair {
    public final int statusCode;
    public final String message;

    public ResponsePair(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
