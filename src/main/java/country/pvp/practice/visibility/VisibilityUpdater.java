package country.pvp.practice.visibility;

import com.google.inject.Inject;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.util.TaskDispatcher;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class VisibilityUpdater {

    private final PlayerManager playerManager;

    public void update(PlayerSession player) {
        update(player, false);
    }

    public void update(PlayerSession player, boolean flicker) {
        if (flicker) {
            for (PlayerSession other : playerManager.getAll()) {
                if (!other.isOnline()) continue;
                update(player, other, Visibility.HIDDEN);
                update(other, player, Visibility.HIDDEN);
            }
        }

        Runnable runnable = () -> {
            for (PlayerSession other : playerManager.getAll()) {
                if (!other.isOnline()) continue;

                update(player, other);
                update(other, player);
            }
        };

        if (flicker) TaskDispatcher.runLater(runnable, 500L, TimeUnit.MILLISECONDS);
        else runnable.run();
    }

    public void update(PlayerSession observer, PlayerSession observable) {
        Visibility visibility = VisibilityProvider.provide(observer, observable);

        update(observer, observable, visibility);
    }

    public void update(PlayerSession observer, PlayerSession observable, Visibility visibility) {
        visibility.apply(observer, observable);
    }
}
