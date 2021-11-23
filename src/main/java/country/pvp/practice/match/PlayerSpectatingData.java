package country.pvp.practice.match;

import country.pvp.practice.player.data.PlayerData;
import lombok.Data;

@Data
public class PlayerSpectatingData implements PlayerData {
    private final Match match;
}
