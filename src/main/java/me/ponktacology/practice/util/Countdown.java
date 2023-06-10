package me.ponktacology.practice.util;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Countdown {

  // Consumes seconds left and supplies whether the countdown should be canceled
  private final Function<Integer, Boolean> consumer;
  private int secondsLeft;

  private Countdown(int secondsLeft, Function<Integer, Boolean> consumer) {
    this.secondsLeft = secondsLeft;
    this.consumer = consumer;
    start();
  }

  public static Countdown of(int secondsLeft, Function<Integer, Boolean> consumer) {
    return new Countdown(secondsLeft, consumer);
  }

  private void start() {
    TaskDispatcher.scheduleSync(
        new BukkitRunnable() {
          @Override
          public void run() {
            if (consumer.apply(secondsLeft--) || secondsLeft < 0) {
              cancel();
            }
          }
        },
        1L,
        TimeUnit.SECONDS);
  }
}
