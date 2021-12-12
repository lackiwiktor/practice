package country.pvp.practice.match;

import com.google.inject.Inject;
import country.pvp.practice.arena.Arena;
import country.pvp.practice.arena.ArenaManager;
import country.pvp.practice.itembar.ItemBarService;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.match.snapshot.InventorySnapshotManager;
import country.pvp.practice.match.team.SoloTeam;
import country.pvp.practice.match.team.Team;
import country.pvp.practice.match.type.FreeForAllMatch;
import country.pvp.practice.match.type.StandardMatch;
import country.pvp.practice.player.PlayerService;
import country.pvp.practice.visibility.VisibilityUpdater;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class MatchProvider {

    private final VisibilityUpdater visibilityUpdater;
    private final LobbyService lobbyService;
    private final MatchManager matchManager;
    private final ItemBarService itemBarService;
    private final ArenaManager arenaManager;
    private final InventorySnapshotManager snapshotManager;
    private final PlayerService playerService;

    public StandardMatch provide(Ladder ladder, boolean ranked, boolean duel, Team teamA, Team teamB) {
        Arena arena = arenaManager.getRandom();
        return new StandardMatch(matchManager, visibilityUpdater, lobbyService, itemBarService, arena, ladder, ranked, duel, snapshotManager, playerService, teamA, teamB);
    }

    public FreeForAllMatch provide(Ladder ladder, SoloTeam... teams) {
        Arena arena = arenaManager.getRandom();
        return new FreeForAllMatch(matchManager, visibilityUpdater, lobbyService, itemBarService, snapshotManager, arena, ladder, false, teams);
    }

}
