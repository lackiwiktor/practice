package country.pvp.practice.player;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class PlayerSaveTask implements Runnable {

    private final PlayerManager playerManager;
    private final PlayerRepository playerRepository;

    @Override
    public void run() {
        for (PlayerSession player : playerManager.getAll()) {
            if (!player.isOnline() || !player.isLoaded()) continue;

            playerRepository.save(player);
        }
    }
}
