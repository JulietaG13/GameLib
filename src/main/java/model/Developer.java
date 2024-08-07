package model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import values.Rol;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Developer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;
    
    @OneToMany(mappedBy = "owner")
    private final Set<Game> developed = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "developer_subscriptions",
            joinColumns = @JoinColumn(name = "developer_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> subscribers = new HashSet<>();
    
    private boolean isDonationsSetup = false;
    
    private String mpPublicKey;
    
    private String mpAccessToken;

    public Developer() {}

    public Developer(User user) {
        this.user = user;
    }

    // UTILITY HELPERS
    
    public void addDeveloped(Game game) {
        developed.add(game);
        game.setOwner(this.getUser());
    }

    public void addSubscriber(User user) {
        subscribers.add(user);
        if (!user.getSubscribedDevelopers().contains(this)) {
            user.subscribe(this);
        }
    }

    public void removeSubscriber(User user) {
        subscribers.remove(user);
        if (user.getSubscribedDevelopers().contains(this)) {
            user.unsubscribe(this);
        }
    }

    // GETTERS - SETTERS

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }
    
    public Set<Game> getDeveloped() {
        return Collections.unmodifiableSet(developed);
    }
    
    public Set<User> getSubscribers() {
        return subscribers;
    }
    
    public boolean isDonationsSetup() {
        return isDonationsSetup;
    }
    
    public void setDonationsSetup(boolean donationsSetup) {
        isDonationsSetup = donationsSetup;
    }
    
    public String getMpPublicKey() {
        return mpPublicKey;
    }
    
    public void setMpPublicKey(String mpPublicKey) {
        this.mpPublicKey = mpPublicKey;
    }
    
    public String getMpAccessToken() {
        return mpAccessToken;
    }
    
    public void setMpAccessToken(String mpAccessToken) {
        this.mpAccessToken = mpAccessToken;
    }
}
