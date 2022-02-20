package country.pvp.practice.util.message;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;

@UtilityClass
public class FormatUtil {

    public static String formatHealthWithHeart(double health) {
        String formatted = "";

        if (health == 0) {
            formatted = ChatColor.RED + "Dead";
        } else {
            formatted = FormatUtil.formatHealth(health) + " " + ChatColor.DARK_RED + FormatUtil.getHeartIcon();
        }

        return formatted;
    }

    public static String cutString(String text, int maxLength) {
        if (text.length() < maxLength) {
            return text;
        }

        int charsLeft = (text.length() - maxLength) + 3;

        return text.substring(0, charsLeft) + "...";
    }

    public static double formatHealth(double health) {
        double dividedHealth = health / 2;

        if (dividedHealth % 1 == 0) {
            return dividedHealth;
        }

        if (dividedHealth % .5 == 0) {
            return dividedHealth;
        }

        if (dividedHealth - ((int) dividedHealth) > .5) {
            return ((int) dividedHealth) + 1;
        } else if (dividedHealth - ((int) dividedHealth) > .25) {
            return ((int) dividedHealth) + .5;
        } else {
            return ((int) dividedHealth);
        }
    }

    public static String getHeartIcon() {
        return StringEscapeUtils.unescapeJava("\u2764");
    }
}
