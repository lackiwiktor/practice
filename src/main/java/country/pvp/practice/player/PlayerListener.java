package country.pvp.practice.player;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    protected final @NotNull PlayerManager playerManager;

    public PracticePlayer get(@NotNull Player player) {
        return playerManager.get(player);
    }

    public PracticePlayer get(@NotNull PlayerEvent event) {
        return get(event.getPlayer());
    }
}
