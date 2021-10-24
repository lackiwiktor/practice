package country.pvp.practice.player;

import com.google.inject.Inject;
import country.pvp.practice.itembar.ItemBar;
import country.pvp.practice.visibility.VisibilityUpdater;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class PreparePlayerListener implements Listener {

    private final PlayerManager playerManager;
    private final PlayerRepository playerRepository;
    private final VisibilityUpdater visibilityUpdater;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void joinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PracticePlayer practicePlayer = new PracticePlayer(player);

        playerManager.add(practicePlayer);
        playerRepository.loadAsync(practicePlayer);

        ItemBar.LOBBY.apply(practicePlayer);
        visibilityUpdater.update(practicePlayer);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void quitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Optional.ofNullable(playerManager.remove(player))
                .ifPresent(playerRepository::saveAsync);
    }
}
