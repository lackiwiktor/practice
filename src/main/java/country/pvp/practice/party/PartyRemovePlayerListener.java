package country.pvp.practice.party;

import com.google.inject.Inject;
import country.pvp.practice.player.PlayerListener;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PracticePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class PartyRemovePlayerListener extends PlayerListener {

    @Inject
    public PartyRemovePlayerListener(PlayerManager playerManager) {
        super(playerManager);
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        PracticePlayer practicePlayer = get(event);

        if (practicePlayer.hasParty()) {
            practicePlayer.handleDisconnectInParty();
        }
    }
}
