package country.pvp.practice.player;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Slf4j
public class PreparePlayerListener extends PlayerListener {

    private final PlayerService playerService;

    @Inject
    public PreparePlayerListener(PlayerManager playerManager, PlayerService playerService) {
        super(playerManager);
        this.playerService = playerService;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void loginEvent(@NotNull AsyncPlayerPreLoginEvent event) {
        PracticePlayer practicePlayer = new PracticePlayer(event.getUniqueId(), event.getName());

        try {
            playerService.load(practicePlayer);
            playerManager.add(practicePlayer);
            practicePlayer.setLoaded(true);
        } catch (Exception e) {
            e.printStackTrace();
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Error");
            log.error("Player not initialized successfully, player= {}", event.getName());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void quitEvent(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Optional.ofNullable(playerManager.remove(player))
                .ifPresent(playerService::saveAsync);
    }
}
