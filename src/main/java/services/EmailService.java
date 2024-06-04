package services;

import example.ImageExample;
import model.News;
import model.User;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.util.Collection;
import java.util.Properties;

public class EmailService {
  
  private final static String EMAIL = "gamelib.bot@gmail.com";
  private final static String PASSWORD = "nokctcdiijpfokyr";
  
  private static final String SMTP_SERVER = "smtp.gmail.com";
  private static final String SMTP_PORT = "465";
  
  private static Session session;
  
  private final static String subject = "News from your subscriptions on GameLib!";
  
  static {
    Properties prop = new Properties();
    prop.put("mail.smtp.host", SMTP_SERVER);
    prop.put("mail.smtp.port", SMTP_PORT);
    prop.put("mail.smtp.auth", "true");
    prop.put("mail.smtp.socketFactory.port", SMTP_PORT);
    prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
  
    session = Session.getInstance(
        prop,
        new Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(EMAIL, PASSWORD);
          }
        });
  }
  
  public static void main(String[] args) {
    try {
      Message message = new MimeMessage(session);
  
      message.setFrom(new InternetAddress(EMAIL));
      message.setRecipients(
          Message.RecipientType.TO,
          InternetAddress.parse("") // add an email
      );
  
      message.setSubject("Testing Gmail SSL 11");
  
      String content = gameNews(
          "Many things done",
          "Updated the game and there are no more error or stuff yay",
          "author123",
          "Awesome Game");
  
      // body
      MimeBodyPart htmlPart = new MimeBodyPart();
      htmlPart.setContent(content, "text/html; charset=utf-8");
      
      // attachment
      MimeBodyPart imgPart = new MimeBodyPart();
      byte[] imageBytes = ImageExample.RED_DOT.image.getBytes();
      DataSource dataSource = new ByteArrayDataSource(imageBytes, "image/png");
      imgPart.setDataHandler(new DataHandler(dataSource));
      imgPart.setContentID("<cover0123>");
      imgPart.setDisposition(MimeBodyPart.INLINE);
      
      // combine
      Multipart multipart = new MimeMultipart("related");
      multipart.addBodyPart(htmlPart);
      multipart.addBodyPart(imgPart);
    
      message.setContent(multipart);
      
      Transport.send(message);
    
    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }
  
  public static boolean sendMail(String emails, News news) {
    try {
      Message message = new MimeMessage(session);
  
      message.setFrom(new InternetAddress(EMAIL));
      message.setRecipients(
          Message.RecipientType.TO,
          InternetAddress.parse(emails)
      );
      message.setSubject(subject);
      
      // body
      MimeBodyPart htmlPart = new MimeBodyPart();
      htmlPart.setContent(getHtml(news), "text/html; charset=utf-8");
  
      // attachment
      MimeBodyPart imgPart = new MimeBodyPart();
      byte[] imageBytes = news.getGame().getCover().getBytes();
      DataSource dataSource = new ByteArrayDataSource(imageBytes, "image/png");
      imgPart.setDataHandler(new DataHandler(dataSource));
      imgPart.setContentID("<cover0123>");
      imgPart.setDisposition(MimeBodyPart.INLINE);
  
      // combine
      Multipart multipart = new MimeMultipart("related");
      multipart.addBodyPart(htmlPart);
      multipart.addBodyPart(imgPart);
  
      message.setContent(multipart);
  
      Transport.send(message);
      
    } catch (MessagingException e) {
      return false;
    }
    return true;
  }
  
  public static String getEmails(Collection<User> users) {
    if (users.isEmpty()) {
      return "";
    }
    StringBuilder emails = new StringBuilder();
    users.forEach(u -> emails.append(u.getEmail()).append(", "));
    emails.delete(emails.length() - 2, emails.length());
    
    return emails.toString();
  }
  
  private static String getHtml(News news) {
    return gameNews(
        news.getTitle(),
        news.getDescription(),
        news.getAuthor().getUsername(),
        news.getGame().getName()
    );
  }
  
  private static String gameNews(String title, String description, String author, String game) {
    return "<html>" +
        "<head>" +
        "    <style>" +
        "        body {" +
        "            font-family: Arial, sans-serif;" +
        "            margin: 0;" +
        "            padding: 0;" +
        "            background-color: #f4f4f4;" +
        "        }" +
        "        .email-container {" +
        "            max-width: 600px;" +
        "            margin: 0 auto;" +
        "            background-color: #ffffff;" +
        "            padding: 20px;" +
        "            border-radius: 10px;" +
        "            box-shadow: 0 2px 5px rgba(0,0,0,0.1);" +
        "        }" +
        "        .header {" +
        "            background-color: #0073e6;" +
        "            color: #ffffff;" +
        "            padding: 10px 20px;" +
        "            border-radius: 10px 10px 0 0;" +
        "            text-align: center;" +
        "        }" +
        "        .content {" +
        "            padding: 20px;" +
        "        }" +
        "        .footer {" +
        "            text-align: center;" +
        "            padding: 10px;" +
        "            font-size: 12px;" +
        "            color: #777777;" +
        "        }" +
        "        .news-image {" +
        "            max-width: 100%;" +
        "            height: auto;" +
        "            border-radius: 10px;" +
        "        }" +
        "    </style>" +
        "</head>" +
        "<body>" +
        "    <div class=\"email-container\">" +
        "        <div class=\"header\">" +
        "            <h1>News</h1>" +
        "        </div>" +
        "        <div class=\"content\">" +
        "            <h2>" + title + "</h2>" +
        "            <p>" + description + "</p>" +
        "            <img class=\"news-image\" src=\"cid:cover0123\" alt=\"Game Cover\"/>" +
        "            <p><strong>Game:</strong> " + game + "</p>" +
        "            <p><strong>Author:</strong> " + author + "</p>" +
        "        </div>" +
        "        <div class=\"footer\">" +
        "            &copy; 2024 GameLib. All rights reserved. /j" +
        "        </div>" +
        "    </div>" +
        "</body>" +
        "</html>";
  }
}

// https://mailtrap.io/blog/java-send-email-gmail/