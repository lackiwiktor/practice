package country.pvp.practice.player;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
    public void playerLogin(AsyncPlayerPreLoginEvent event) {
        if (Bukkit.getPlayer(event.getUniqueId()) != null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "You are already connected to this server.");
            return;
        }

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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void playerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        Optional.ofNullable(playerManager.remove(player))
                .ifPresent(playerService::saveAsync);
    }
}
