package server.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;

public class ZonedDateTimeSerializer implements JsonSerializer<ZonedDateTime> {
    public ZonedDateTimeSerializer() {
    }

    public JsonElement serialize(ZonedDateTime zonedDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("year", zonedDateTime.getYear());
        result.addProperty("month", zonedDateTime.getMonthValue());
        result.addProperty("day", zonedDateTime.getDayOfMonth());
        result.addProperty("hour", zonedDateTime.getHour());
        result.addProperty("minute", zonedDateTime.getMinute());
        result.addProperty("second", zonedDateTime.getSecond());
        result.addProperty("nano", zonedDateTime.getNano());
        result.addProperty("zoneId", zonedDateTime.getZone().toString());
        return result;
    }
}
