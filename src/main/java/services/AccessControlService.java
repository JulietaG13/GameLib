package services;

import entities.Response;
import entities.Token;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Date;
import java.util.Optional;

public class AccessControlService {
    
    private static final String SECRET_KEY = "12000dpi0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static Response isFormatValid(User user) {
        if (user.getUsername() == null) {
            return new Response(true, 404, "Username cannot be null!");
        }

        if (user.getUsername().equals("")) {
            return new Response(true, 404, "Username cannot be empty!");
        }

        if (user.getEmail() == null) {
            return new Response(true, 404, "Email cannot be null!");
        }

        if (user.getEmail().equals("")) {
            return new Response(true, 404, "Email cannot be empty!");
        }

        if (user.getPassword() == null) {
            return new Response(true, 404, "Password cannot be null!");
        }

        if (user.getPassword().equals("")) {
            return new Response(true, 404, "Password cannot be empty!");
        }

        return new Response(false, 200);
    }

    public static Response isUserAvailable(User user, EntityManager em) {
        final UserService userService = new UserService(em);

        if (userService.usernameInUse(user)) {
            return new Response(true, 403, "Username already exists!");
        }
        if (userService.emailInUse(user)) {
            return new Response(true, 403, "Email already in use!");
        }
        return new Response(false, 200);
    }

    public static Response authenticateUser(String username, String password, EntityManager entityManager) {
        UserService userService = new UserService(entityManager);
        EntityTransaction tx = entityManager.getTransaction();

        tx.begin();
        Optional<User> possibleUser = userService.findByUsername(username);
        tx.commit();

        if (possibleUser.isEmpty()) {
            return new Response(true, 404, "User does not exist!");
        }

        if (!possibleUser.get().getPassword().equals(password)) {
            return new Response(true, 404, "Password is incorrect!");
        }
        return new Response(false, 200);
    }
    
    
    public static Token generateToken(String username) {
        // Create JWT token with username as subject
        String str = Jwts.builder()
            .setSubject(username)
            .setExpiration(new Date(System.currentTimeMillis() + 3_600_000)) // 1 hour expiration
            .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
            .compact();
        return new Token(str);
    }
}
