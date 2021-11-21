package country.pvp.practice.match;

import com.google.common.collect.Sets;
import country.pvp.practice.ladder.Ladder;

import java.util.Set;

public class MatchManager {

    private final Set<Match> matches = Sets.newHashSet();

    public void add(Match match) {
        matches.add(match);
    }

    public void remove(Match match) {
        matches.remove(match);
    }

    public int getPlayersInFightCount(Ladder ladder, boolean ranked) {
        return matches.stream().filter(it -> it.getLadder().equals(ladder) && it.isRanked() == ranked).mapToInt(Match::getPlayersCount).sum();
    }

    public int getPlayersInFight() {
        return matches.stream().mapToInt(Match::getPlayersCount).sum();
    }

}
