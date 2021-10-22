package country.pvp.practice.message;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public class Messager {

    public static void message(Recipient recipient, String message) {
        recipient.receive(message);
    }

    public static void message(Player recipient, String message) {
        recipient.sendMessage(message);
    }

    public static void message(Recipient recipient, String... messages) {
        for (String message : messages) {
            message(recipient, message);
        }
    }

    public static void message(Player recipient, String... messages) {
        for (String message : messages) {
            message(recipient, message);
        }
    }

    public static void messageError(Recipient recipient, String error) {
        message(recipient, "Error: ".concat(error));
    }

    public static void messageError(Player recipient, String error) {
        message(recipient, "Error: ".concat(error));
    }

    public static void message(Recipient recipient, Messages message) {
        message(recipient, message.get());
    }

    public static void message(Recipient recipient, Messages message, String placeholder, Object value) {
        message(recipient, message.match(placeholder, value));
    }

    public static void message(Recipient recipient, Messages message, MessagePattern... patterns) {
        message(recipient, message.match(patterns));
    }
}
