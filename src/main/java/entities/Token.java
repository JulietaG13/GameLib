package entities;

import com.google.gson.Gson;

public class Token {
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
}
