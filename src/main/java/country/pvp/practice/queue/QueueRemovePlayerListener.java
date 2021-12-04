package country.pvp.practice.queue;

import com.google.inject.Inject;
import country.pvp.practice.player.PlayerListener;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class QueueRemovePlayerListener extends PlayerListener {

    @Inject
    public QueueRemovePlayerListener(PlayerManager playerManager) {
        super(playerManager);
    }

    @EventHandler
    public void playerQuit( PlayerQuitEvent event) {
        PlayerSession playerSession = get(event);

        if (playerSession.isInQueue()) {
            playerSession.removeFromQueue();
        }
    }
}
