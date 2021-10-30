package country.pvp.practice.queue;

import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.team.PlayerTeam;

public class SoloQueue extends Queue<PlayerTeam> {
    public SoloQueue(Ladder ladder, MatchType type) {
        super(ladder, type);
    }

    public void addToQueue(PlayerTeam team) {
        team.startQueuing(this.add(team));
    }
}
