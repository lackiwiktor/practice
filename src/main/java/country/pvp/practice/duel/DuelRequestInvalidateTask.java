package country.pvp.practice.duel;

import com.google.inject.Inject;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DuelRequestInvalidateTask implements Runnable {

    private final PlayerManager playerManager;

    @Override
    public void run() {
        for (PracticePlayer practicePlayer : playerManager.getAll()) {
            if (!practicePlayer.isLoaded() || !practicePlayer.isOnline()) continue;
            practicePlayer.invalidateDuelRequests();
        }
    }
}
