package country.pvp.practice.visibility;

import com.google.inject.Inject;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class VisibilityUpdater {

    private final PlayerManager playerManager;

    public void update(PlayerSession player) {
        for (PlayerSession other : playerManager.getAll()) {
            if (!other.isOnline()) continue;

            update(player, other);
            update(other, player);
        }
    }

    public void update(PlayerSession observer, PlayerSession observable) {
        if (observer.equals(observable)) return;
        Visibility visibility = VisibilityProvider.provide(observer, observable);
        visibility.apply(observer, observable);
    }
}
