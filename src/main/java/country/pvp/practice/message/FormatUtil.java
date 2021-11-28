package country.pvp.practice.message;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FormatUtil {

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
}
