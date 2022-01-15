package country.pvp.practice.util;

import lombok.experimental.UtilityClass;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class TimeUtil {

    public static long elapsed(long start) {
        return System.currentTimeMillis() - start;
    }

    public static long convertTimeToTicks(long time, TimeUnit unit) {
        return TimeUnit.MILLISECONDS.convert(time, unit) / 50;
    }

    public static String formatTimeMillisToClock(long millis) {
        return millis / 1000L <= 0
                ? "0:00"
                : String.format(
                "%01d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis)
                        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    public static String millisToSeconds(long millis) {
        return new DecimalFormat("#0.0").format(millis / 1000.0F);
    }
}
