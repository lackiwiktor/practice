package country.pvp.practice.util.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
@UtilityClass
public class PotionEffectAdapter {

  public static String toJsonString(PotionEffect potionEffect) {
    return toJson(potionEffect).toString();
  }

  public static @Nullable JsonObject toJson(@Nullable PotionEffect potionEffect) {
    if (potionEffect == null) {
      return (null);
    }

    JsonObject jsonObject = new JsonObject();

    jsonObject.addProperty("id", potionEffect.getType().getId());
    jsonObject.addProperty("duration", potionEffect.getDuration());
    jsonObject.addProperty("amplifier", potionEffect.getAmplifier());
    jsonObject.addProperty("ambient", potionEffect.isAmbient());

    return jsonObject;
  }

  public static @Nullable PotionEffect fromJson(@Nullable JsonElement jsonElement) {
    if (jsonElement == null || !jsonElement.isJsonObject()) {
      return (null);
    }

    JsonObject jsonObject = new JsonObject();

    PotionEffectType effectType = PotionEffectType.getById(jsonObject.get("id").getAsInt());
    int duration = jsonObject.get("duration").getAsInt();
    int amplifier = jsonObject.get("amplifier").getAsInt();
    boolean ambient = jsonObject.get("ambient").getAsBoolean();

    return (new PotionEffect(effectType, duration, amplifier, ambient));
  }

  public static @Nullable PotionEffect fromJson(String json) {
    JsonElement jsonElement = new JsonParser().parse(json);
    return fromJson(jsonElement);
  }
}
