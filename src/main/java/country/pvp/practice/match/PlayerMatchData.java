package country.pvp.practice.match;

import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.player.data.PlayerData;
import lombok.Data;

@Data
public class PlayerMatchData implements PlayerData {

    private final Match match;
    private final PlayerMatchStatistics statistics = new PlayerMatchStatistics();
    private boolean dead;
    private boolean disconnected;
    private PracticePlayer lastAttacker;

    public void handleHit() {
        statistics.handleHit();
    }

    public void handleBeingHit(PracticePlayer player) {
        statistics.handleBeingHit();
        lastAttacker = player;
    }

    public void increaseMissedPotions() {
        statistics.increaseMissedPotions();
    }

    public void increaseThrownPotions() {
        statistics.increasedThrownPotions();
    }
}
