package country.pvp.practice.visibility;

import com.google.inject.Inject;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class VisibilityUpdater {

    private final @NotNull PlayerManager playerManager;
    private final @NotNull VisibilityProvider visibilityProvider;

    public void update(@NotNull PracticePlayer player) {
        for (PracticePlayer other : playerManager.getAll()) {
            if (!other.isOnline()) continue;

            update(player, other);
            update(other, player);
        }
    }

    public void update(@NotNull PracticePlayer observer, @NotNull PracticePlayer observable) {
        Visibility visibility = visibilityProvider.provide(observer, observable);
        visibility.apply(observer, observable);
    }
}
