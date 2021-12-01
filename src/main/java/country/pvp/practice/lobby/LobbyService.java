package country.pvp.practice.lobby;

import com.google.inject.Inject;
import country.pvp.practice.itembar.ItemBarManager;
import country.pvp.practice.match.RematchData;
import country.pvp.practice.match.SoloMatch;
import country.pvp.practice.player.PlayerUtil;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.player.data.PlayerState;
import country.pvp.practice.settings.PracticeSettings;
import country.pvp.practice.visibility.VisibilityUpdater;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class LobbyService {

    private final VisibilityUpdater visibilityUpdater;
    private final ItemBarManager itemBarManager;
    private final PracticeSettings practiceSettings;

    public void moveToLobby(PracticePlayer player, SoloMatch match) {
        player.setState(PlayerState.IN_LOBBY, new PlayerLobbyData(match.isRanked() ? null : new RematchData(match.getPlayerOpponent(player), match.getLadder())));
        moveToLobby0(player);
    }

    public void moveToLobby(PracticePlayer player) {
        player.setState(PlayerState.IN_LOBBY, new PlayerLobbyData(null));
        itemBarManager.apply(player);
        moveToLobby0(player);
    }

    private void moveToLobby0(PracticePlayer player) {
        PlayerUtil.resetPlayer(player.getPlayer());
        player.disableFlying();
        itemBarManager.apply(player);
        visibilityUpdater.update(player);
        player.teleport(practiceSettings.getSpawnLocation());
    }
}
