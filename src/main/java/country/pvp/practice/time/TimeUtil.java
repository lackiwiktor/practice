package country.pvp.practice.time;

import lombok.experimental.UtilityClass;

import java.util.concurrent.TimeUnit;

@UtilityClass
public class TimeUtil {

    public static long elapsed(long start) {
        return System.currentTimeMillis() - start;
    }

    public static long convertTimeToTicks(long time, TimeUnit unit) {
        return TimeUnit.MILLISECONDS.convert(time, unit) / 50;
    }
}
