package country.pvp.practice.match;

import country.pvp.practice.arena.Arena;
import country.pvp.practice.itembar.ItemBarManager;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.match.snapshot.InventorySnapshotManager;
import country.pvp.practice.match.team.PartyTeam;
import country.pvp.practice.visibility.VisibilityUpdater;

public class PartyMatch extends Match<PartyTeam> {

    PartyMatch(VisibilityUpdater visibilityUpdater, LobbyService lobbyService, MatchManager matchManager, ItemBarManager itemBarManager, InventorySnapshotManager snapshotManager, Ladder ladder, Arena arena, PartyTeam teamA, PartyTeam teamB) {
        super(visibilityUpdater, lobbyService, matchManager, itemBarManager, snapshotManager, ladder, arena, teamA, teamB, false, true);
    }
}
