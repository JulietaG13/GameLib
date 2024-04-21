package services;

import entities.responses.MessageResponse;
import entities.responses.StatusResponse;
import entities.Token;
import entities.responses.UserResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Date;
import java.util.Optional;

public class AccessControlService {
    
    private static final String SECRET_KEY = "12000dpi0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static StatusResponse isFormatValid(User user) {
        StatusResponse usernameResponse = isUsernameValid(user.getUsername());
        if (usernameResponse.hasError()) {
            return usernameResponse;
        }
        
        StatusResponse emailResponse = isEmailValid(user.getPassword());
        if (emailResponse.hasError()) {
            return emailResponse;
        }
        
        StatusResponse passwordResponse = isPasswordValid(user.getPassword());
        if (passwordResponse.hasError()) {
            return passwordResponse;
        }
        
        return new StatusResponse(false, 200);
    }
    
    public static StatusResponse isUsernameValid(String username) {
        MessageResponse messageResponse = User.isUsernameValid(username);
        if(messageResponse.hasError()) {
            return new StatusResponse(true, 404, messageResponse.getMessage());
        }
        return new StatusResponse(false, 200);
    }
    
    public static StatusResponse isPasswordValid(String password) {
        MessageResponse messageResponse = User.isPasswordValid(password);
        if(messageResponse.hasError()) {
            return new StatusResponse(true, 404, messageResponse.getMessage());
        }
        return new StatusResponse(false, 200);
    }
    
    public static StatusResponse isEmailValid(String email) {
        MessageResponse messageResponse = User.isEmailValid(email);
        if(messageResponse.hasError()) {
            return new StatusResponse(true, 404, messageResponse.getMessage());
        }
        return new StatusResponse(false, 200);
    }

    public static StatusResponse isUserAvailable(User user, EntityManager em) {
        final UserService userService = new UserService(em);

        if (userService.usernameInUse(user)) {
            return new StatusResponse(true, 403, "Username already exists!");
        }
        if (userService.emailInUse(user)) {
            return new StatusResponse(true, 403, "Email already in use!");
        }
        return new StatusResponse(false, 200);
    }

    public static UserResponse authenticateUser(String username, String password, EntityManager entityManager) {
        UserService userService = new UserService(entityManager);
        EntityTransaction tx = entityManager.getTransaction();

        tx.begin();
        Optional<User> possibleUser = userService.findByUsername(username);
        tx.commit();

        if (possibleUser.isEmpty()) {
            return new UserResponse(true, null, 404, "User does not exist!");
        }

        if (!possibleUser.get().getPassword().equals(password)) {
            return new UserResponse(true, null, 404, "Password is incorrect!");
        }
        return new UserResponse(false, possibleUser.get(), 200);
    }
    
    //   TOKENS   //
    
    public static Token generateToken(String username) {
        // Create JWT token with username as subject
        String str = Jwts.builder()
            .setSubject(username)
            .setExpiration(new Date(System.currentTimeMillis() + 3_600_000)) // 3_600_000 = 1 hour expiration
            .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
            .compact();
        return new Token(str);
    }
    
    public static String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(SECRET_KEY)
            .build()
            .parseClaimsJws(token)
            .getBody();
        return claims.getSubject();
    }
    
    public static boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;   // expired or just wrong
        }
    }
}
