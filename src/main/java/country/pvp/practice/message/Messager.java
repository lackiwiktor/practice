package country.pvp.practice.message;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class Messager {

    public static void message(@NotNull Recipient recipient, String message) {
        recipient.receive(MessageUtil.color(message));
    }

    public static void message(@NotNull Player recipient, String message) {
        recipient.sendMessage(MessageUtil.color(message));
    }

    public static void message(@NotNull Recipient recipient, String @NotNull ... messages) {
        for (String message : messages) {
            message(recipient, message);
        }
    }

    public static void message(@NotNull Player recipient, String @NotNull ... messages) {
        for (String message : messages) {
            message(recipient, message);
        }
    }

    public static void messageError(@NotNull Recipient recipient, @NotNull String error) {
        message(recipient, ChatColor.RED + "Error: ".concat(error));
    }

    public static void messageError(@NotNull Player recipient, @NotNull String error) {
        message(recipient, ChatColor.RED + "Error: ".concat(error));
    }

    public static void messageSuccess(@NotNull Recipient recipient, @NotNull String message) {
        message(recipient, ChatColor.GREEN.toString().concat(message));
    }

    public static void messageSuccess(@NotNull Player recipient, @NotNull String message) {
        message(recipient, ChatColor.GREEN.toString().concat(message));
    }

    public static void message(@NotNull Recipient recipient, @NotNull Messages message) {
        message(recipient, message.get());
    }

    public static void message(@NotNull Recipient recipient, @NotNull Messages message, String placeholder, Object value) {
        message(recipient, message.match(placeholder, value));
    }

    public static void message(@NotNull Recipient recipient, @NotNull Messages message, MessagePattern... patterns) {
        message(recipient, message.match(patterns));
    }
}
