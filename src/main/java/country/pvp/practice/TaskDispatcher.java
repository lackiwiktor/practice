package country.pvp.practice;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@UtilityClass
public class TaskDispatcher {

  private static final ExecutorService EXECUTOR_SERVICE =
      Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2);

  public static Future<?> async(Runnable runnable) {
    return EXECUTOR_SERVICE.submit(runnable);
  }
}
