package country.pvp.practice.lobby;

import country.pvp.practice.match.RematchData;
import country.pvp.practice.player.data.PlayerData;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
public class PlayerLobbyData implements PlayerData {
    private @Nullable final RematchData rematchData;
}
