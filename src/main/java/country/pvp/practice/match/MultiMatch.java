package country.pvp.practice.match;

import com.google.common.collect.Sets;
import country.pvp.practice.arena.Arena;
import country.pvp.practice.itembar.ItemBarService;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.match.snapshot.InventorySnapshotManager;
import country.pvp.practice.match.team.PartyTeam;
import country.pvp.practice.message.component.ChatComponentBuilder;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.visibility.VisibilityUpdater;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Set;

public class MultiMatch extends Match<PartyTeam> {

    MultiMatch(VisibilityUpdater visibilityUpdater, LobbyService lobbyService, MatchManager matchManager, ItemBarService itemBarService, InventorySnapshotManager snapshotManager, Ladder ladder, Arena arena, PartyTeam teamA, PartyTeam teamB) {
        super(visibilityUpdater, lobbyService, matchManager, itemBarService, snapshotManager, ladder, arena, teamA, teamB, false, true);
    }

    @Override
    void updateTeamVisibility() {
        for (PlayerSession session : teamA.getOnlinePlayers()) {
            for (PlayerSession other : teamB.getOnlinePlayers()) {
                visibilityUpdater.update(session, other);
                visibilityUpdater.update(other, session);
            }
        }
    }

    @Override
    void movePlayersToLobby() {
        for (PlayerSession session : teamA.getOnlinePlayers()) {
            lobbyService.moveToLobby(session);
        }

        for (PlayerSession session : teamB.getOnlinePlayers()) {
            lobbyService.moveToLobby(session);
        }
    }

    @Override
    void createInventorySnapshots() {
        for (PlayerSession session : teamA.getOnlinePlayers()) {
            if(teamA.isAlive(session)) createInventorySnapshot(session);
        }

        for (PlayerSession session : teamB.getOnlinePlayers()) {
            if(teamB.isAlive(session)) createInventorySnapshot(session);
        }
    }

    @Override
    Set<PlayerSession> getOnlinePlayers() {
        Set<PlayerSession> onlinePlayers = Sets.newHashSet(spectators);
        onlinePlayers.addAll(teamA.getOnlinePlayers());
        onlinePlayers.addAll(teamB.getOnlinePlayers());
        return onlinePlayers;
    }

    @Override
    public BaseComponent[] createComponent(PartyTeam team, boolean winner) {
        ChatComponentBuilder builder = new ChatComponentBuilder(winner ? ChatColor.GREEN + "Winner: " : ChatColor.RED + "Loser: ");

        for (PlayerSession player : team.getPlayers()) {
            builder.append(createComponent(player));
        }

        return builder.create();
    }

    @Override
    public List<String> getBoard(PlayerSession session) {
        return null;
    }
}
