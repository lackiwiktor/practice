package country.pvp.practice.match;

import country.pvp.practice.arena.Arena;
import country.pvp.practice.itembar.ItemBarManager;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.match.snapshot.InventorySnapshotManager;
import country.pvp.practice.match.team.PartyTeam;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.visibility.VisibilityUpdater;

import java.util.Collections;
import java.util.List;

public class PartyMatch extends Match<PartyTeam> {

    PartyMatch(VisibilityUpdater visibilityUpdater, LobbyService lobbyService, MatchManager matchManager, ItemBarManager itemBarManager, InventorySnapshotManager snapshotManager, Ladder ladder, Arena arena, PartyTeam teamA, PartyTeam teamB) {
        super(visibilityUpdater, lobbyService, matchManager, itemBarManager, snapshotManager, ladder, arena, teamA, teamB, false, true);
    }

    @Override
    public List<String> getBoard(PlayerSession session) {
        return Collections.emptyList();
    }
}
