package model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Developer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    @ManyToMany
    @JoinTable(
            name = "developer_subscriptions",
            joinColumns = @JoinColumn(name = "developer_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> subscribers = new HashSet<>();

    public Developer() {}

    public Developer(User user) {
        this.user = user;
    }

    // UTILITY HELPERS

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

    public Set<User> getSubscribers() {
        return subscribers;
    }
}
