package model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification implements interfaces.Notification, Comparable<Notification> {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne
  private User owner;
  
  private String description;
  
  private LocalDateTime dateTime;
  
  public Notification() {}
  
  public Notification(
      User owner,
      String description
  ) {
    this.owner = owner;
    this.description = description;
    dateTime = LocalDateTime.now();
    
    owner.addNotification(this);
  }
  
  // GETTERS AND SETTERS
  
  public void setId(Long id) {
    this.id = id;
  }
  
  @Override
  public Long getId() {
    return id;
  }
  
  @Override
  public User getOwner() {
    return owner;
  }
  
  @Override
  public String getDescription() {
    return description;
  }
  
  @Override
  public LocalDateTime getDateTime() {
    return dateTime;
  }
  
  public void setDateTime(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }
  
  // OTHER
  
  @Override
  public int compareTo(Notification other) {
    return other.dateTime.compareTo(this.dateTime); // Descending order
  }
}
