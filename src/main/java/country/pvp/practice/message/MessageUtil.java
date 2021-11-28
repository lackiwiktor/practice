package country.pvp.practice.message;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public class MessageUtil {

    public static @NotNull String color(@NotNull String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
