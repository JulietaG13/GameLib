package model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    public User(String username, String email, String password, Rol rol) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    public User(String username, String email, String password) {
        this(username, email, password, Rol.USER);
    }
}
