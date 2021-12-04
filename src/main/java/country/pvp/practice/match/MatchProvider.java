package country.pvp.practice.match;

import com.google.inject.Inject;
import country.pvp.practice.arena.ArenaManager;
import country.pvp.practice.itembar.ItemBarManager;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.match.snapshot.InventorySnapshotManager;
import country.pvp.practice.match.team.PartyTeam;
import country.pvp.practice.match.team.SoloTeam;
import country.pvp.practice.visibility.VisibilityUpdater;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class MatchProvider {

    private final VisibilityUpdater visibilityUpdater;
    private final LobbyService lobbyService;
    private final MatchManager matchManager;
    private final ItemBarManager itemBarManager;
    private final ArenaManager arenaManager;
    private final InventorySnapshotManager snapshotManager;

    public Match<?> provide(Ladder ladder, boolean ranked, boolean duel, SoloTeam teamA, SoloTeam teamB) {
        return new SoloMatch(visibilityUpdater, lobbyService, matchManager, itemBarManager, snapshotManager, ladder, arenaManager.getRandom(), teamA, teamB, ranked, duel);
    }

    public Match<?> provide(Ladder ladder, PartyTeam teamA, PartyTeam teamB) {
        return new PartyMatch(visibilityUpdater, lobbyService, matchManager, itemBarManager, snapshotManager, ladder, arenaManager.getRandom(), teamA, teamB);
    }

}
