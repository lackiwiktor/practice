package country.pvp.practice.match;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import country.pvp.practice.arena.Arena;
import country.pvp.practice.itembar.ItemBarService;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.match.elo.EloUtil;
import country.pvp.practice.match.snapshot.InventorySnapshotManager;
import country.pvp.practice.match.team.SoloTeam;
import country.pvp.practice.message.component.ChatComponentBuilder;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.visibility.VisibilityUpdater;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SoloMatch extends Match<SoloTeam> {

    SoloMatch(VisibilityUpdater visibilityUpdater, LobbyService lobbyService, MatchManager matchManager, ItemBarService itemBarService, InventorySnapshotManager snapshotManager, Ladder ladder, Arena arena, SoloTeam teamA, SoloTeam teamB, boolean ranked, boolean duel) {
        super(visibilityUpdater, lobbyService, matchManager, itemBarService, snapshotManager, ladder, arena, teamA, teamB, ranked, duel);
    }

    public PlayerSession getPlayerOpponent(PlayerSession player) {
        return getOpponent(player).getPlayerSession();
    }

    @Override
    void movePlayersToLobby() {
        PlayerSession playerSession = teamA.getPlayerSession();
        if (!teamA.hasDisconnected(playerSession))
            lobbyService.moveToLobby(playerSession);

        PlayerSession opponentSession = teamB.getPlayerSession();
        if (!teamB.hasDisconnected(playerSession))
            lobbyService.moveToLobby(opponentSession);
    }

    @Override
    void createInventorySnapshots() {
        PlayerSession playerSession = teamA.getPlayerSession();
        if (teamA.isAlive(playerSession))
            createInventorySnapshot(playerSession);

        PlayerSession opponentSession = teamB.getPlayerSession();
        if (teamB.isAlive(playerSession))
            createInventorySnapshot(opponentSession);
    }

    @Override
    public BaseComponent[] createComponent(SoloTeam team, boolean winner) {
        ChatComponentBuilder builder = new ChatComponentBuilder(winner ? ChatColor.GREEN + "Winner: " : ChatColor.RED + "Loser: ");

        builder.append(createComponent(teamA.getPlayerSession()));
        builder.append(createComponent(teamB.getPlayerSession()));

        return builder.create();
    }

    @Override
    void updateTeamVisibility() {
        PlayerSession playerSession = teamA.getPlayerSession();
        PlayerSession opponentSession = teamB.getPlayerSession();

        visibilityUpdater.update(playerSession, opponentSession);
        visibilityUpdater.update(opponentSession, playerSession);
    }


    @Override
    void end(@Nullable SoloTeam winner) {
        if (ranked && winner != null) {
            SoloTeam loser = getOpponent(winner);
            int winnerNewRating = EloUtil.getNewRating(winner.getElo(ladder), loser.getElo(ladder), true);
            int loserNewRating = EloUtil.getNewRating(loser.getElo(ladder), winner.getElo(ladder), false);

            loser.setElo(ladder, loserNewRating);
            winner.setElo(ladder, winnerNewRating);
        }

        PlayerSession playerSession = teamA.getPlayerSession();
        PlayerSession opponentSession = teamB.getPlayerSession();

        playerSession.setRematchData(new RematchData(opponentSession, ladder));
        opponentSession.setRematchData(new RematchData(playerSession, ladder));

        super.end(winner);
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

    @Override
    Set<PlayerSession> getOnlinePlayers() {
        Set<PlayerSession> onlinePlayers = Sets.newHashSet(spectators);
        onlinePlayers.add(teamA.getPlayerSession());
        onlinePlayers.add(teamB.getPlayerSession());
        return onlinePlayers;
    }

}
