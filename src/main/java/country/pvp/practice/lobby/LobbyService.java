package country.pvp.practice.lobby;

import com.google.inject.Inject;
import country.pvp.practice.itembar.ItemBarManager;
import country.pvp.practice.itembar.ItemBarType;
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

    public void moveToLobby(PracticePlayer player) {
        player.setState(PlayerState.IN_LOBBY);
        player.removeStateData();
        PlayerUtil.resetPlayer(player.getPlayer());
        player.disableFlying();
        itemBarManager.apply(ItemBarType.LOBBY, player);
        visibilityUpdater.update(player);
        player.teleport(practiceSettings.getSpawnLocation());
    }
}
