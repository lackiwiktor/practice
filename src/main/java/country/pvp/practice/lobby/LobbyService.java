package country.pvp.practice.lobby;

import com.google.inject.Inject;
import country.pvp.practice.itembar.ItemBarService;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.player.data.PlayerState;
import country.pvp.practice.settings.PracticeSettings;
import country.pvp.practice.util.PlayerUtil;
import country.pvp.practice.visibility.VisibilityUpdater;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;


@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class LobbyService {

    private final VisibilityUpdater visibilityUpdater;
    private final ItemBarService itemBarService;
    private final PracticeSettings practiceSettings;

    public Location getSpawnLocation() {
        return practiceSettings.getSpawnLocation();
    }

    public boolean shouldRebound(Location location) {
        return location.getY() < practiceSettings.getVoidY();
    }

    public void moveToLobby(PlayerSession player) {
        player.setState(PlayerState.IN_LOBBY);
        PlayerUtil.resetPlayer(player.getPlayer());
        player.disableFlying();
        itemBarService.apply(player);
        visibilityUpdater.update(player);
        player.teleport(practiceSettings.getSpawnLocation());
    }
}
