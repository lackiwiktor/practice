package country.pvp.practice.lobby;

import country.pvp.practice.duel.RematchData;
import country.pvp.practice.player.data.PlayerData;
import lombok.Data;

@Data
public class PlayerLobbyData implements PlayerData {
    private final RematchData rematchData;
}
