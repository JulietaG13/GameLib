package adapters;

import com.google.gson.JsonDeserializer;
import java.time.LocalDateTime;

public class GsonAdapter {
  
  public static JsonDeserializer<LocalDateTime> getLocalDateTimeAdapter() {
    return (json, type, jsonDeserializationContext) -> {
      String str = json.getAsString();
      LocalDateTime localDateTime;
      try {
        localDateTime = LocalDateTime.parse(str);
      } catch (Exception e) {
        localDateTime = LocalDateTime.now();
      }
      return localDateTime;
    };
  }
}
