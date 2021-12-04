package country.pvp.practice.duel;

import com.google.inject.Inject;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DuelRequestInvalidateTask implements Runnable {

    private final PlayerManager playerManager;

    @Override
    public void run() {
        for (PlayerSession playerSession : playerManager.getAll()) {
            if (!playerSession.isLoaded() || !playerSession.isOnline()) continue;
            playerSession.invalidateDuelRequests();
        }
    }
}
