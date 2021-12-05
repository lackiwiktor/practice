package country.pvp.practice.match;

import com.google.common.collect.Lists;
import country.pvp.practice.arena.Arena;
import country.pvp.practice.itembar.ItemBarManager;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.match.snapshot.InventorySnapshotManager;
import country.pvp.practice.match.team.SoloTeam;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.visibility.VisibilityUpdater;

import java.util.List;
import java.util.Optional;

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

    @Override
    public List<String> getBoard(PlayerSession session) {
        List<String> lines = Lists.newArrayList();
        PlayerSession opponent = getPlayerOpponent(session);

        switch (state) {
            case COUNTDOWN:
                lines.add(session.getName());
                lines.add("vs");
                lines.add(opponent.getName());
                break;
            case END:
                Optional<SoloTeam> winnerOptional = getWinner();
                String winner = winnerOptional.isPresent() ? winnerOptional.get().getName() : "None";
                lines.add("Winner: " + winner);
                break;
            case FIGHT:
                lines.add("Your Ping: " + session.getPing());
                lines.add("Their Ping: " + opponent.getPing());
                break;
        }

        return lines;
    }
}
