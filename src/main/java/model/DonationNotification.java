package model;

import javax.persistence.Entity;

@Entity
public class DonationNotification extends Notification {
  
  private String preferenceId;
  
  private Long amount;
  
  public DonationNotification() {
    super();
  }
  
  public DonationNotification(User developer, String preferenceId, Long amount) {
    super(developer, getDescription(amount));
    this.preferenceId = preferenceId;
    this.amount = amount;
  }
  
  private static String getDescription(Long amount) {
    return "You received a donation of $" + amount + "!";
  }
  
  public String getPreferenceId() {
    return preferenceId;
  }
  
  public Long getAmount() {
    return amount;
  }
}
