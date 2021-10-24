package country.pvp.practice.player;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

@Slf4j
public class PreparePlayerListener extends PlayerListener {

    private final PlayerRepository playerRepository;

    @Inject
    public PreparePlayerListener(PlayerManager playerManager, PlayerRepository playerRepository) {
        super(playerManager);
        this.playerRepository = playerRepository;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void joinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PracticePlayer practicePlayer = new PracticePlayer(player);

        try {
            playerRepository.loadAsync(practicePlayer);
            playerManager.add(practicePlayer);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Player not initialized successfully, player= {}", player.getName());
            player.kickPlayer("&cError");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void quitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Optional.ofNullable(playerManager.remove(player))
                .ifPresent(playerRepository::saveAsync);
    }
}
