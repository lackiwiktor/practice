package me.ponktacology.practice.util.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class LocationAdapter {

    public static @Nullable String toJson(@Nullable Location location) {
        if (location == null) {
            return (null);
        }

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("world", location.getWorld().getName());
        jsonObject.addProperty("x", location.getX());
        jsonObject.addProperty("y", location.getY());
        jsonObject.addProperty("z", location.getZ());
        jsonObject.addProperty("yaw", location.getYaw());
        jsonObject.addProperty("pitch", location.getPitch());

        return jsonObject.toString();
    }

    public static @Nullable Location fromJson(@Nullable String json) {
        if (json == null) return null;
        JsonElement jsonElement = new JsonParser().parse(json);
        if (jsonElement == null || !jsonElement.isJsonObject()) {
            return (null);
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        World world = Bukkit.getWorld(jsonObject.get("world").getAsString());
        double x = jsonObject.get("x").getAsDouble();
        double y = jsonObject.get("y").getAsDouble();
        double z = jsonObject.get("z").getAsDouble();
        float yaw = jsonObject.get("yaw").getAsFloat();
        float pitch = jsonObject.get("pitch").getAsFloat();

        return (new Location(world, x, y, z, yaw, pitch));
    }
}
