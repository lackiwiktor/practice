package country.pvp.practice.listeners;

import com.google.inject.Inject;
import country.pvp.practice.player.PlayerListener;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerRepository;
import country.pvp.practice.player.PlayerSession;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;


public class SessionListener extends PlayerListener {

    private final PlayerRepository playerRepository;

    @Inject
    public SessionListener(PlayerManager playerManager, PlayerRepository playerRepository) {
        super(playerManager);
        this.playerRepository = playerRepository;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void playerLogin(AsyncPlayerPreLoginEvent event) {
        if (Bukkit.getPlayer(event.getUniqueId()) != null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "You are already connected to this server.");
            return;
        }

        PlayerSession playerSession = new PlayerSession(event.getUniqueId(), event.getName());

        try {
            playerRepository.load(playerSession);
            playerSession.setLoaded(true);
            playerManager.add(playerSession);
        } catch (Exception e) {
            e.printStackTrace();
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Error");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void playerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        if (get(event) == null)
            event.getPlayer().kickPlayer("Not initialized correctly.");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        Optional.ofNullable(playerManager.remove(player))
                .ifPresent(playerRepository::saveAsync);
    }
}
