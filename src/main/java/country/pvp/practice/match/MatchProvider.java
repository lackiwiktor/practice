package country.pvp.practice.match;

import country.pvp.practice.arena.Arena;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.team.Team;
import country.pvp.practice.visibility.VisibilityUpdater;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MatchProvider {

    private final VisibilityUpdater visibilityUpdater;

    public Match provide(Ladder ladder, Arena arena, boolean ranked, Team teamA, Team teamB) {
        return new Match(visibilityUpdater, ladder, arena, teamA, teamB, ranked);
    }
}
