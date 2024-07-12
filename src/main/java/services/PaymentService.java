package services;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.resources.preference.PreferenceItem;
import model.Developer;
import model.DonationNotification;
import model.Notification;
import repositories.DonationNotificationRepository;
import repositories.NotificationRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentService {
  
  public static void main(String[] args) {
    // testing
    MercadoPagoConfig.setAccessToken("APP_USR-3308950100823866-070817-edee83028d45f993e55a22ec7b955a62-1893394530");  // Access Token
  
    PreferenceItemRequest itemRequest =
        PreferenceItemRequest.builder()
            .id("1234")
            .title("Games")
            .description("PS5")
            .pictureUrl("https://picture.com/PS5")
            .categoryId("games")
            .quantity(1)
            .currencyId("ARS")
            .unitPrice(new BigDecimal("4000"))
            .build();
    
    List<PreferenceItemRequest> items = new ArrayList<>();
    items.add(itemRequest);
    
    PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
        .success("http://localhost:3000")
        .failure("http://localhost:3000")
        .build();
    
    PreferenceRequest preferenceRequest = PreferenceRequest.builder()
        .items(items)
        .backUrls(backUrls)
        .build();
    
    PreferenceClient client = new PreferenceClient();
  
    try {
      Preference preference = client.create(preferenceRequest);
      
      String initPoint = preference.getInitPoint();
      String id = preference.getId();
  
      System.out.println("init point:" + initPoint);
      System.out.println();
      System.out.println(id);
      
    } catch (MPException | MPApiException e) {
      throw new RuntimeException(e);
    }
  
  }
  
  public static String createPreference(Developer developer, String amount, String backUrl) throws MPException, MPApiException {
    MercadoPagoConfig.setAccessToken(developer.getMpAccessToken());
    System.out.println("\nAccess Token: " + developer.getMpAccessToken());
  
    PreferenceItemRequest itemRequest =
        PreferenceItemRequest.builder()
            .id(developer.getUser().getUsername() + amount + LocalDateTime.now())
            .title("Donation for " + developer.getUser().getUsername())
            .quantity(1)
            .currencyId("ARS")
            .unitPrice(new BigDecimal(amount))
            .build();
  
    PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
        .success(backUrl)
        .pending(backUrl)
        .failure(backUrl)
        .build();
  
    PreferenceRequest preferenceRequest = PreferenceRequest.builder()
        .items(List.of(itemRequest))
        .backUrls(backUrls)
        .expires(true)
        .expirationDateTo(OffsetDateTime.of(LocalDateTime.now().plusHours(1), ZoneOffset.ofHours(-3)))
        .build();
  
    PreferenceClient client = new PreferenceClient();
    
    Preference preference = client.create(preferenceRequest);
  
    String initPoint = preference.getInitPoint();
    String id = preference.getId();
  
    System.out.println("init point:" + initPoint);
    System.out.println();
    System.out.println(id);
    
    return id;
  }
  
  public static long getAmountPayed(Developer developer, String preferenceId) throws MPException, MPApiException {
    MercadoPagoConfig.setAccessToken(developer.getMpAccessToken());
    System.out.println("\nAccess Token: " + developer.getMpAccessToken());
    
    Preference preference = new PreferenceClient().get(preferenceId);
    
    long amount = 0L;
    for (PreferenceItem i : preference.getItems()) {
      amount += (i.getQuantity() * i.getUnitPrice().longValue());
    }
  
    return amount;
  }
  
  public static void notifyDeveloper(Developer developer, String preferenceId, long amount, EntityManagerFactory factory) {
    DonationNotification notification = new DonationNotification(developer.getUser(), preferenceId, amount);
    
    EntityManager em = factory.createEntityManager();
    DonationNotificationRepository notificationRepository = new DonationNotificationRepository(em);
    
    Optional<DonationNotification> repeated = notificationRepository.findByPreferenceId(preferenceId);
    
    if (repeated.isPresent()) {
      Long repeatedId = repeated.get().getOwner().getId();
      Long developerId = developer.getUser().getId();
      if (repeatedId.equals(developerId)) {
        return;
      }
    }
  
    notificationRepository.persist(notification);
  }
  
  public static void notifyErrorToDeveloper(Developer developer, EntityManagerFactory factory) {
    Notification notification = new Notification(
        developer.getUser(),
        "Someone tried to send you a donation but couldn't. Please check your payment credentials."
    );
    
    EntityManager em = factory.createEntityManager();
    new NotificationRepository(em).persist(notification);
    em.close();
  }
}
