package country.pvp.practice.match;

import com.google.inject.Inject;
import country.pvp.practice.arena.Arena;
import country.pvp.practice.itembar.ItemBarManager;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.team.Team;
import country.pvp.practice.visibility.VisibilityUpdater;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class MatchProvider {

    private final @NotNull VisibilityUpdater visibilityUpdater;
    private final @NotNull LobbyService lobbyService;
    private final @NotNull MatchManager matchManager;
    private final @NotNull ItemBarManager itemBarManager;

    public @NotNull Match provide(Ladder ladder, Arena arena, boolean ranked, Team teamA, Team teamB) {
        return new Match(visibilityUpdater, lobbyService, matchManager, itemBarManager, ladder, arena, teamA, teamB, ranked);
    }
}
