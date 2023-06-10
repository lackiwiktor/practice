package me.ponktacology.practice.util;

import me.ponktacology.practice.Practice;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

@UtilityClass
public class TaskDispatcher {

    public static BukkitTask async(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(Practice.getPractice(), runnable);
    }

    public static BukkitTask scheduleSync(Runnable runnable, long duration, TimeUnit unit) {
        return Bukkit.getScheduler().runTaskTimer(Practice.getPractice(), runnable, 0L, TimeUtil.convertTimeToTicks(duration, unit));
    }

    public static BukkitTask scheduleSync(BukkitRunnable runnable, long duration, TimeUnit unit) {
        return runnable.runTaskTimer(Practice.getPractice(), 0L, TimeUtil.convertTimeToTicks(duration, unit));
    }

    public static BukkitTask scheduleAsync(Runnable runnable, long duration, TimeUnit unit) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(Practice.getPractice(), runnable, 0L, TimeUtil.convertTimeToTicks(duration, unit));
    }

    public static void runLater(Runnable runnable, long duration, TimeUnit unit) {
        Bukkit.getScheduler().runTaskLater(Practice.getPractice(), runnable, TimeUtil.convertTimeToTicks(duration, unit));
    }

    public static void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(Practice.getPractice(), runnable);
    }
}
