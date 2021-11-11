package country.pvp.practice.match;

import com.google.inject.Inject;
import country.pvp.practice.arena.Arena;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.team.Team;
import country.pvp.practice.visibility.VisibilityUpdater;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class MatchProvider {

    private final VisibilityUpdater visibilityUpdater;
    private final LobbyService lobbyService;

    public Match provide(Ladder ladder, Arena arena, boolean ranked, Team teamA, Team teamB) {
        return new Match(visibilityUpdater, lobbyService, ladder, arena, teamA, teamB, ranked);
    }
}
