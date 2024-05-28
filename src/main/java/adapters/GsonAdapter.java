package adapters;

import com.google.gson.JsonDeserializer;

import java.time.LocalDate;

public class GsonAdapter {
  
  public static JsonDeserializer<LocalDate> getLocalDateAdapter() {
    return (json, type, jsonDeserializationContext) -> {
      String str = json.getAsString();
      LocalDate localDate;
      try {
        localDate = LocalDate.parse(str);
      } catch (Exception e) {
        localDate = LocalDate.now();
      }
      return localDate;
    };
  }
}
