package country.pvp.practice.player;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class PlayerSaveTask implements Runnable {

    private final @NotNull PlayerManager playerManager;
    private final @NotNull PlayerService playerService;

    @Override
    public void run() {
        for (PracticePlayer player : playerManager.getAll()) {
            if(!player.isLoaded()) continue;

            playerService.saveAsync(player);
        }
    }
}
