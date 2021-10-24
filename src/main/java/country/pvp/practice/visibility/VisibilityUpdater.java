package country.pvp.practice.visibility;

import com.google.inject.Inject;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class VisibilityUpdater {

    private final PlayerManager playerManager;
    private final VisibilityProvider visibilityProvider;

    public void update(PracticePlayer player) {
        for (PracticePlayer other : playerManager.getAll()) {
            update(player, other);
            update(other, player);
        }
    }

    public void update(PracticePlayer observer, PracticePlayer observable) {
        Visibility visibility = visibilityProvider.provide(observer, observable);
        visibility.apply(observer, observable);
    }
}
