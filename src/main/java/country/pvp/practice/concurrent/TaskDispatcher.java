package country.pvp.practice.concurrent;

import country.pvp.practice.PracticePlugin;
import country.pvp.practice.time.TimeUtil;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class TaskDispatcher {

    private static final ExecutorService EXECUTOR_SERVICE =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2);

    public static Future<?> async( Runnable runnable) {
        return EXECUTOR_SERVICE.submit(runnable);
    }

    public static BukkitTask scheduleSync(Runnable runnable, long duration, TimeUnit unit) {
        return Bukkit.getScheduler().runTaskTimer(PracticePlugin.getPlugin(PracticePlugin.class), runnable, 0L, TimeUtil.convertTimeToTicks(duration, unit));
    }

    public static BukkitTask scheduleAsync(Runnable runnable, long duration, TimeUnit unit) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(PracticePlugin.getPlugin(PracticePlugin.class), runnable, 0L, TimeUtil.convertTimeToTicks(duration, unit));
    }

    public static void runLater(Runnable runnable, long duration, TimeUnit unit) {
        Bukkit.getScheduler().runTaskLater(PracticePlugin.getPlugin(PracticePlugin.class), runnable, TimeUtil.convertTimeToTicks(duration, unit));
    }
}
