package services;

import values.ErrorMessages;
import values.responses.ErrorResponse;
import values.responses.StatusResponse;
import values.responses.UserResponse;
import values.Token;
import interfaces.Responses;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import model.User;
import repositories.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Date;
import java.util.Optional;

public class AccessControlService {
    
    private static final String SECRET_KEY = "12000dpi0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static Responses isFormatValid(User user) {
        Responses usernameResponse = isUsernameValid(user.getUsername());
        if (usernameResponse.hasError()) {
            return usernameResponse;
        }

        Responses emailResponse = isEmailValid(user.getEmail());
        if (emailResponse.hasError()) {
            return emailResponse;
        }

        Responses passwordResponse = isPasswordValid(user.getPassword());
        if (passwordResponse.hasError()) {
            return passwordResponse;
        }
        
        return new StatusResponse(200);
    }
    
    public static Responses isUsernameValid(String username) {
        Responses response = User.isUsernameValid(username);
        if(response.hasError()) {
            return new ErrorResponse(404, response.getMessage());
        }
        return new StatusResponse(200);
    }
    
    public static Responses isPasswordValid(String password) {
        Responses response = User.isPasswordValid(password);
        if(response.hasError()) {
            return new ErrorResponse(404, response.getMessage());
        }
        return new StatusResponse(200);
    }
    
    public static Responses isEmailValid(String email) {
        Responses response = User.isEmailValid(email);
        if(response.hasError()) {
            return new ErrorResponse(404, response.getMessage());
        }
        return new StatusResponse(200);
    }

    public static Responses isUserAvailable(User user, EntityManager em) {
        final UserRepository userRepository = new UserRepository(em);

        if (userRepository.usernameInUse(user)) {
            return new ErrorResponse(403, "Username already exists!");
        }
        if (userRepository.emailInUse(user)) {
            return new ErrorResponse(403, "Email already in use!");
        }
        return new StatusResponse(200);
    }

    public static Responses authenticateUser(String username, String password, EntityManager entityManager) {
        UserRepository userRepository = new UserRepository(entityManager);
        EntityTransaction tx = entityManager.getTransaction();

        tx.begin();
        Optional<User> possibleUser = userRepository.findByUsername(username);
        tx.commit();

        if (possibleUser.isEmpty()) {
            return new ErrorResponse(404, ErrorMessages.invalidCredentials());
        }

        if (!possibleUser.get().getPassword().equals(password)) {
            return new ErrorResponse(404, ErrorMessages.invalidCredentials());
        }
        
        if (possibleUser.get().isBanned()) {
            return new ErrorResponse(403, "You are banned");
        }
        return new UserResponse(possibleUser.get());
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
