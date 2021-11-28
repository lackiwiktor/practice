package country.pvp.practice.match;

import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.player.data.PlayerData;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class PlayerMatchData implements PlayerData {
    private final @NotNull Match match;
    private boolean dead;
    private boolean disconnected;
    private PracticePlayer lastAttacker;
}
