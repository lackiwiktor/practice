package country.pvp.practice.match;

import com.google.inject.Inject;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class PearlCooldownTask implements Runnable {

    private final PlayerManager playerManager;

    @Override
    public void run() {
        for (PracticePlayer practicePlayer : playerManager.getAll()) {
            if (!practicePlayer.isLoaded() || !practicePlayer.isOnline() || !practicePlayer.isInMatch()) continue;

            PlayerMatchData matchData = practicePlayer.getStateData();

            if (matchData.hasPearlCooldownExpired()) {
                matchData.notifyAboutPearlCooldownExpiration(practicePlayer);
            } else {
                int seconds = Math.round(matchData.getPearlCooldownRemaining()) / 1_000;
                Player player = practicePlayer.getPlayer();
                player.setLevel(seconds);
                player.setExp(matchData.getPearlCooldownRemaining() / 16_000.0F);
            }
        }
    }
}
