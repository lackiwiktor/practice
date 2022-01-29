package country.pvp.practice.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Nullable;

public class RegionAdapter {

    public static @Nullable String toJson(@Nullable Region region) {
        if (region == null) {
            return (null);
        }

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("minX", region.getMinX());
        jsonObject.addProperty("maxX", region.getMaxX());
        jsonObject.addProperty("minY", region.getMinY());
        jsonObject.addProperty("maxY", region.getMaxY());
        jsonObject.addProperty("minZ", region.getMinZ());
        jsonObject.addProperty("maxZ", region.getMaxZ());

        return jsonObject.toString();
    }

    public static @Nullable Region fromJson(@Nullable String json) {
        if (json == null) return null;
        JsonElement jsonElement = new JsonParser().parse(json);
        if (jsonElement == null || !jsonElement.isJsonObject()) {
            return (null);
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        int minX = jsonObject.get("minX").getAsInt();
        int maxX = jsonObject.get("maxX").getAsInt();
        int minY = jsonObject.get("minY").getAsInt();
        int maxY = jsonObject.get("maxY").getAsInt();
        int minZ = jsonObject.get("minZ").getAsInt();
        int maxZ = jsonObject.get("maxZ").getAsInt();

        return new Region(minX, maxX, minY, maxY, minZ, maxZ);
    }
}
