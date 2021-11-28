package country.pvp.practice.player;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class PlayerSaveTask implements Runnable {

    private final PlayerManager playerManager;
    private final PlayerService playerService;

    @Override
    public void run() {
        for (PracticePlayer player : playerManager.getAll()) {
            if (!player.isLoaded()) continue;

            playerService.saveAsync(player);
        }

        log.info("Saved " + playerManager.getAll().size() + " players.");
    }
}
