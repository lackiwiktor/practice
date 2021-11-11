package country.pvp.practice.message;

import org.bukkit.ChatColor;

public class MessageUtil {

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
