package model;

import com.google.gson.Gson;
import entities.Response.MessageResponse;
import entities.Response.StatusResponse;

import javax.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue(generator = "userGen", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column()
    private String biography;

    @Column(nullable = false)
    private Rol rol;

    //TODO(pfp, banner)

    /*
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private final List<Publication> publications = new ArrayList<>();

    @ManyToMany(mappedBy = "liked")
    private final List<Publication> likes = new ArrayList<>();
    */

    public User() {}

    public static UserBuilder create(String username) {
        return new UserBuilder(username);
    }

    private User(UserBuilder builder) {
        this.username = builder.username;
        this.email = builder.email;
        this.password = builder.password;
        this.rol = builder.rol;
    }

    public static User fromJson(String json) {
        final Gson gson = new Gson();
        return gson.fromJson(json, User.class);
    }

    public String asJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static class UserBuilder {
        private final String username;
        private String email;
        private String password;
        private Rol rol;

        public UserBuilder(String username) {
            this.username = username;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder rol(Rol rol) {
            this.rol = rol;
            return this;
        }

        public User build() {
            if(rol == null) rol = Rol.USER;
            return new User(this);
        }

    }
    
    // RESTRICTIONS //
    
    public static MessageResponse isUsernameValid(String username) {
        if (username == null) {
            return new MessageResponse(true, "Username cannot be null!");
        }
        if (username.equals("")) {
            return new MessageResponse(true, "Username cannot be empty!");
        }
        return new MessageResponse(false);
    }
    
    public static MessageResponse isPasswordValid(String password) {
        if (password == null) {
            return new MessageResponse(true, "Password cannot be null!");
        }
        if (password.equals("")) {
            return new MessageResponse(true, "Password cannot be empty!");
        }
        return new MessageResponse(false);
    }
    
    public static MessageResponse isEmailValid(String email) {
        if (email == null) {
            return new MessageResponse(true, "Email cannot be null!");
        }
        if (email.equals("")) {
            return new MessageResponse(true, "Email cannot be empty!");
        }
        return new MessageResponse(false);
    }
    
    // GETTERS - SETTER //

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
