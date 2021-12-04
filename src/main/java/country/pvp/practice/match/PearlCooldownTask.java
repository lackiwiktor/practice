package country.pvp.practice.match;

import com.google.inject.Inject;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class PearlCooldownTask implements Runnable {

    private final PlayerManager playerManager;

    @Override
    public void run() {
        for (PlayerSession playerSession : playerManager.getAll()) {
            if (!playerSession.isLoaded() || !playerSession.isOnline() || !playerSession.isInMatch()) continue;

            if (playerSession.hasPearlCooldownExpired()) {
                playerSession.notifyAboutPearlCooldownExpiration(playerSession);
            } else {
                int seconds = Math.round(playerSession.getRemainingPearlCooldown()) / 1_000;
                Player player = playerSession.getPlayer();
                player.setLevel(seconds);
                player.setExp(playerSession.getRemainingPearlCooldown() / 16_000.0F);
            }
        }
    }
}
