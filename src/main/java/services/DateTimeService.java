package services;

import java.time.LocalDateTime;

public class DateTimeService {
  public static String getDate(LocalDateTime localDateTime) {
    return localDateTime.toLocalDate().toString();
  }
  
  public static String getTime(LocalDateTime localDateTime) {
    return localDateTime.getHour() + ":" + localDateTime.getMinute();
  }
}
