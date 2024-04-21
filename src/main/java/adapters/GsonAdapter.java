package adapters;

import com.google.gson.JsonDeserializer;
import java.time.LocalDateTime;

public class GsonAdapter {
  
  public static JsonDeserializer<LocalDateTime> getLocalDateTimeAdapter() {
    return (json, type, jsonDeserializationContext) -> {
      LocalDateTime localDateTime = LocalDateTime.parse(json.getAsString());
      return localDateTime;
    };
  }
}
