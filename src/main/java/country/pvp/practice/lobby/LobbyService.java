package country.pvp.practice.lobby;

import com.google.inject.Inject;
import country.pvp.practice.itembar.ItemBarManager;
import country.pvp.practice.match.RematchData;
import country.pvp.practice.match.SoloMatch;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.player.PlayerUtil;
import country.pvp.practice.player.data.PlayerState;
import country.pvp.practice.settings.PracticeSettings;
import country.pvp.practice.visibility.VisibilityUpdater;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;


@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class LobbyService {

    private final VisibilityUpdater visibilityUpdater;
    private final ItemBarManager itemBarManager;
    private final PracticeSettings practiceSettings;

    public Location getSpawnLocation() {
        return practiceSettings.getSpawnLocation();
    }

    public void moveToLobby(PlayerSession player, SoloMatch match) {
        player.setState(PlayerState.IN_LOBBY, new SessionLobbyData(match.isRanked() ? null : new RematchData(match.getPlayerOpponent(player), match.getLadder())));
        moveToLobby0(player);
    }

    public void moveToLobby(PlayerSession player) {
        player.setState(PlayerState.IN_LOBBY, player.isInLobby() ? player.getData() : new SessionLobbyData(null));
        itemBarManager.apply(player);
        moveToLobby0(player);
    }

    private void moveToLobby0(PlayerSession player) {
        PlayerUtil.resetPlayer(player.getPlayer());
        player.disableFlying();
        itemBarManager.apply(player);
        visibilityUpdater.update(player);
        player.teleport(practiceSettings.getSpawnLocation());
    }
}
