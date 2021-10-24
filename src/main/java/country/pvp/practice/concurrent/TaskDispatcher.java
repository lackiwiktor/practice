package country.pvp.practice.concurrent;

import country.pvp.practice.Practice;
import country.pvp.practice.time.TimeUtil;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class TaskDispatcher {

  private static final ExecutorService EXECUTOR_SERVICE =
      Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2);

  public static Future<?> async(Runnable runnable) {
    return EXECUTOR_SERVICE.submit(runnable);
  }

  public static BukkitTask scheduleSync(Runnable runnable, long duration, TimeUnit unit) {
    return Bukkit.getScheduler().runTaskTimer(Practice.getPlugin(Practice.class), runnable, 0L, TimeUtil.convertTimeToTicks(duration, unit));
  }

  public static BukkitTask scheduleAsync(Runnable runnable, long duration, TimeUnit unit) {
    return Bukkit.getScheduler().runTaskTimerAsynchronously(Practice.getPlugin(Practice.class), runnable, 0L, TimeUtil.convertTimeToTicks(duration, unit));
  }
}
