package values;

import com.google.gson.Gson;

public class Token {
    public static final String PROPERTY_NAME = "token";
    private final String token;

    public Token(String token) {
        this.token = token;
    }

    public static Token fromJson(String json) {
        final Gson gson = new Gson();
        return gson.fromJson(json, Token.class);
    }

    public String asJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    
    public String getToken() {
        return token;
    }
}
