package country.pvp.practice.match;

import com.google.inject.Inject;
import country.pvp.practice.arena.Arena;
import country.pvp.practice.itembar.ItemBarManager;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.match.snapshot.InventorySnapshotManager;
import country.pvp.practice.match.team.Team;
import country.pvp.practice.visibility.VisibilityUpdater;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class MatchProvider {

    private final VisibilityUpdater visibilityUpdater;
    private final LobbyService lobbyService;
    private final MatchManager matchManager;
    private final ItemBarManager itemBarManager;
    private final InventorySnapshotManager snapshotManager;

    public Match provide(Ladder ladder, Arena arena, boolean ranked, Team teamA, Team teamB) {
        return new Match(visibilityUpdater, lobbyService, matchManager, itemBarManager, snapshotManager, ladder, arena, teamA, teamB, ranked);
    }
}
