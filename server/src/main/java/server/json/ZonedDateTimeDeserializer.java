package server.json;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ZonedDateTimeDeserializer implements JsonDeserializer<ZonedDateTime> {
    public ZonedDateTimeDeserializer() {
    }

    public ZonedDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int year = jsonObject.get("year").getAsInt();
        int month = jsonObject.get("month").getAsInt();
        int day = jsonObject.get("day").getAsInt();
        int hour = jsonObject.get("hour").getAsInt();
        int minute = jsonObject.get("minute").getAsInt();
        int second = jsonObject.get("second").getAsInt();
        int nano = jsonObject.get("nano").getAsInt();
        String zoneId = jsonObject.get("zoneId").getAsString();
        ZoneId parsedZoneId = ZoneId.of(zoneId);
        return ZonedDateTime.of(year, month, day, hour, minute, second, nano, parsedZoneId);
    }
}