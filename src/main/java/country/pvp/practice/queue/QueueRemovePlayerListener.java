package country.pvp.practice.queue;

import com.google.inject.Inject;
import country.pvp.practice.player.PlayerListener;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PracticePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class QueueRemovePlayerListener extends PlayerListener {

    @Inject
    public QueueRemovePlayerListener(PlayerManager playerManager) {
        super(playerManager);
    }

    @EventHandler
    public void quitEvent(@NotNull PlayerQuitEvent event) {
        PracticePlayer practicePlayer = get(event);

        if (!practicePlayer.isInQueue()) return;

        PlayerQueueData queueData = practicePlayer.getStateData();
        queueData.getQueue().removePlayer(practicePlayer, false);
    }
}
