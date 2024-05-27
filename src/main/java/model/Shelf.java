package model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
    
    private boolean isPrivate = false;
    
    @ManyToOne
    private User owner;
    
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "game_in_shelf",
            joinColumns = @JoinColumn(name = "shelf_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    private final Set<Game> games = new HashSet<>();

    public Shelf() {}

    public Shelf(User owner, String name, boolean isPrivate) {
        this.owner = owner;
        this.name = name;
        this.isPrivate = isPrivate;
    }
    
    public Shelf(User owner, String name) {
        this(owner, name, false);
    }

    // JSON //

    public JsonObject asJson() {
        JsonArray array = new JsonArray();
        for (Game game : games) {
            array.add(game.asJson());
        }

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("id", id);
        jsonObj.addProperty("name", name);
        jsonObj.addProperty("owner_id", owner.getId());
        jsonObj.addProperty("is_private", isPrivate);
        jsonObj.add("games", array);
        return jsonObj;
    }

    // ADDS? //

    public void addGame(Game game) {
        games.add(game);
        game.addInShelf(this);
    }
    
    public void takeOutGame(Game game) {
        games.remove(game);
        game.removeFromShelf(this);
    }

    // GETTERS SETTERS //

    public Long getId() {
        return id;
    }

    public List<Game> getGames() {
        return new ArrayList<>(games);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    public boolean isPrivate() {
        return isPrivate;
    }
    
    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
    
    public void setPrivate() {
        this.isPrivate = true;
    }
    
    public void setPublic() {
        this.isPrivate = false;
    }
}
