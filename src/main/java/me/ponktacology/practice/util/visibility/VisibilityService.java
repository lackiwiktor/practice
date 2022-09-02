package me.ponktacology.practice.util.visibility;

import me.ponktacology.practice.Service;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.player.PlayerService;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.Logger;
import me.ponktacology.practice.util.TaskDispatcher;

import java.util.concurrent.TimeUnit;


public class VisibilityService extends Service {

  public void update(PracticePlayer player) {
    update(player, false);
  }

  public void update(PracticePlayer player, boolean flicker) {
    if (flicker) {
      for (PracticePlayer other : Practice.getService(PlayerService.class).getAll()) {
        update(player, other, Visibility.HIDDEN);
        update(other, player, Visibility.HIDDEN);
      }
    }

    Runnable runnable =
        () -> {
          for (PracticePlayer other : Practice.getService(PlayerService.class).getAll()) {
            update(player, other);
            update(other, player);
          }
        };

    if (flicker) TaskDispatcher.runLater(runnable, 500L, TimeUnit.MILLISECONDS);
    else runnable.run();
  }

  public void update(PracticePlayer observer, PracticePlayer observable) {
    Visibility visibility = VisibilityProvider.provide(observer, observable);

    update(observer, observable, visibility);
  }

  public void update(PracticePlayer observer, PracticePlayer observable, Visibility visibility) {
    if (observer.equals(observable)) return;
    if (!observer.isOnline() || !observable.isOnline()) {
      Logger.log(
          "Couldn't update player visibility of %s (online: %b) to %s (online: %b)",
          observer.getName(), observer.isOnline(), observable.getName(), observable.isOnline());
      return;
    }

    Logger.debug(
        "Updating player visibility of %s (online: %b) to %s (online: %b)",
        observer.getName(), observer.isOnline(), observable.getName(), observable.isOnline());
    //visibility.apply(observer, observable);
  }
}
