package country.pvp.practice.match;

import com.google.common.collect.Sets;
import country.pvp.practice.ladder.Ladder;

import java.util.Set;

public class MatchManager {

    private final Set<Match> matches = Sets.newCopyOnWriteArraySet();

    public void add(Match match) {
        matches.remove(match);
    }

    public void remove(Match match) {
        matches.remove(match);
    }

    public int getPlayersInFightCount(Ladder ladder, boolean ranked) {
        return matches.stream().filter(it -> it.getLadder().equals(ladder) && it.isRanked() == ranked).mapToInt(it -> it.getPlayersCount()).sum();
    }

    public int getPlayersInFight() {
        return matches.stream().mapToInt(it -> it.getPlayersCount()).sum();
    }

}
