package country.pvp.practice.match;

import country.pvp.practice.player.PracticePlayer;
import lombok.Data;

@Data
public class MatchData {
    private final Match match;
    private boolean dead;
    private boolean disconnected;
    private PracticePlayer lastAttacker;
}
