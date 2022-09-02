package me.ponktacology.practice;

import me.ponktacology.practice.util.TaskDispatcher;
import lombok.Getter;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class Service {
  @Getter private final Set<Object> commands = new HashSet<>();
  @Getter private final Set<Listener> listeners = new HashSet<>();

  public void start() {
    configure();
  }

  protected void configure() {}

  public void stop() {}

  protected void addListener(Listener listener) {
    listeners.add(listener);
  }

  protected void addCommand(Object command) {
    commands.add(command);
  }

  protected void registerTask(Runnable runnable, long delay, TimeUnit unit, boolean async) {
    if (async) {
      TaskDispatcher.scheduleAsync(runnable, delay, unit);
    } else TaskDispatcher.scheduleSync(runnable, delay, unit);
  }
}
