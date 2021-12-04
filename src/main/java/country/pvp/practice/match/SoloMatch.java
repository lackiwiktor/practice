package country.pvp.practice.match;

import country.pvp.practice.arena.Arena;
import country.pvp.practice.itembar.ItemBarManager;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.match.snapshot.InventorySnapshotManager;
import country.pvp.practice.match.team.SoloTeam;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.visibility.VisibilityUpdater;

public class SoloMatch extends Match<SoloTeam> {

    SoloMatch(VisibilityUpdater visibilityUpdater, LobbyService lobbyService, MatchManager matchManager, ItemBarManager itemBarManager, InventorySnapshotManager snapshotManager, Ladder ladder, Arena arena, SoloTeam teamA, SoloTeam teamB, boolean ranked, boolean duel) {
        super(visibilityUpdater, lobbyService, matchManager, itemBarManager, snapshotManager, ladder, arena, teamA, teamB, ranked, duel);
    }

    public PlayerSession getPlayerOpponent(PlayerSession player) {
        return getOpponent(player).getPlayer();
    }

    @Override
    void movePlayersToLobby() {
        for (PlayerSession player : getAllOnlinePlayers()) {
            lobbyService.moveToLobby(player, this);
        }

        for (PlayerSession spectator : spectators) {
            stopSpectating(spectator, false);
        }
    }
}
