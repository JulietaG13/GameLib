package model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Shelf {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    private User user;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "game_in_shelf",
            joinColumns = @JoinColumn(name = "shelf_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    private final transient Set<Game> games = new HashSet<>();

    public Shelf() {}

    public Shelf(User user, String name) {
        this.user = user;
        this.name = name;
    }

    public void addGame(Game game) {
        games.add(game);
        game.addInShelf(this);
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }
}
