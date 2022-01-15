package country.pvp.practice.match.type;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import country.pvp.practice.Messages;
import country.pvp.practice.arena.Arena;
import country.pvp.practice.itembar.ItemBarService;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.MatchManager;
import country.pvp.practice.match.RematchData;
import country.pvp.practice.match.elo.EloUtil;
import country.pvp.practice.match.snapshot.InventorySnapshot;
import country.pvp.practice.match.snapshot.InventorySnapshotManager;
import country.pvp.practice.match.team.Team;
import country.pvp.practice.match.team.type.SoloTeam;
import country.pvp.practice.player.PlayerService;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.util.message.MessagePattern;
import country.pvp.practice.visibility.VisibilityUpdater;

import java.util.List;


public class TeamMatch extends Match {

    private final PlayerService playerService;
    private final Team teamA;
    private final Team teamB;

    public TeamMatch(MatchManager matchManager, VisibilityUpdater visibilityUpdater, LobbyService lobbyService, ItemBarService itemBarService, Arena arena, Ladder ladder, boolean ranked, boolean duel, InventorySnapshotManager snapshotManager, PlayerService playerService, Team teamA, Team teamB) {
        super(snapshotManager, matchManager, visibilityUpdater, lobbyService, itemBarService, arena, ladder, ranked, duel);
        this.playerService = playerService;
        this.teamA = teamA;
        this.teamB = teamB;
    }

    @Override
    protected void prepareTeams() {
        prepareTeam(teamA);
        prepareTeam(teamB);
    }

    @Override
    protected void resetTeams() {
        Preconditions.checkNotNull(arena.getSpawnLocation1());
        Preconditions.checkNotNull(arena.getSpawnLocation2());
        resetTeam(teamA, arena.getSpawnLocation1());
        resetTeam(teamB, arena.getSpawnLocation2());

        updateVisibility(true);
    }

    @Override
    public boolean canStartRound() {
        return false;
    }

    @Override
    protected void handleEnd() {
        if (winner instanceof SoloTeam && getOpponent(winner) instanceof SoloTeam) {
            SoloTeam loserTeam = (SoloTeam) getOpponent(winner);
            SoloTeam winnerTeam = (SoloTeam) winner;

            if (ranked) {
                int winnerNewRating = EloUtil.getNewRating(winnerTeam.getElo(ladder), loserTeam.getElo(ladder), true);
                int loserNewRating = EloUtil.getNewRating(loserTeam.getElo(ladder), winnerTeam.getElo(ladder), false);

                loserTeam.setElo(ladder, loserNewRating);
                winnerTeam.setElo(ladder, winnerNewRating);

                playerService.saveAsync(winnerTeam.getPlayerSession());
                playerService.saveAsync(loserTeam.getPlayerSession());
            }

            PlayerSession playerSession = winnerTeam.getPlayerSession();
            PlayerSession opponentSession = loserTeam.getPlayerSession();

            playerSession.setRematchData(new RematchData(opponentSession, ladder));
            opponentSession.setRematchData(new RematchData(playerSession, ladder));
        }
    }

    @Override
    protected void movePlayersToLobby() {
        for (PlayerSession session : teamA.getOnlinePlayers()) {
            lobbyService.moveToLobby(session);
        }

        for (PlayerSession session : teamB.getOnlinePlayers()) {
            lobbyService.moveToLobby(session);
        }
    }

    @Override
    protected boolean canEndMatch() {
        return true;
    }

    @Override
    protected void broadcastPlayerDisconnect(PlayerSession disconnectedPlayer) {
        broadcastPlayerDisconnect(teamA, disconnectedPlayer);
        broadcastPlayerDisconnect(teamB, disconnectedPlayer);
    }

    @Override
    protected void broadcastPlayerDeath(PlayerSession player) {
        if (player.hasLastAttacker()) {
            PlayerSession killer = player.getLastAttacker();

            broadcast(teamA,
                    Messages.MATCH_PLAYER_KILLED_BY_PLAYER.match(
                            new MessagePattern("{player}", getFormattedDisplayName(player, teamA)),
                            new MessagePattern("{killer}", getFormattedDisplayName(killer, teamA))));
            broadcast(teamB,
                    Messages.MATCH_PLAYER_KILLED_BY_PLAYER.match(
                            new MessagePattern("{player}", getFormattedDisplayName(player, teamB)),
                            new MessagePattern("{killer}", getFormattedDisplayName(killer, teamB))));
        } else {
            broadcast(teamA,
                    Messages.MATCH_PLAYER_KILLED_BY_UNKNOWN.match("{player}", getFormattedDisplayName(player, teamA)));
            broadcast(teamB,
                    Messages.MATCH_PLAYER_KILLED_BY_UNKNOWN.match("{player}", getFormattedDisplayName(player, teamB)));
        }
    }

    @Override
    protected boolean canEndRound() {
        if (teamA.isDead()) {
            winner = teamA;
            return true;
        } else if (teamB.isDead()) {
            winner = teamB;
            return true;
        }

        return false;
    }


    public Team getTeam(PlayerSession player) {
        return teamA.hasPlayer(player) ? teamA : teamB;
    }

    public Team getOpponent(Team team) {
        return team.equals(teamA) ? teamB : teamA;
    }

    @Override
    public void receive(String message) {
        teamA.receive(message);
        teamB.receive(message);
    }

    @Override
    protected InventorySnapshot createInventorySnapshot(PlayerSession player) {
        InventorySnapshot snapshot = super.createInventorySnapshot(player);

        Team team = getTeam(player);
        Team opponent = getOpponent(team);

        if (opponent instanceof SoloTeam) {
            SoloTeam soloOpponent = (SoloTeam) opponent;
            PlayerSession opponentPlayer = soloOpponent.getPlayerSession();
            InventorySnapshot inventorySnapshot = snapshots.get(opponentPlayer);

            if (inventorySnapshot != null) {
                inventorySnapshot.setOpponent(snapshot.getId());
                snapshot.setOpponent(inventorySnapshot.getId());
            }
        }

        return snapshot;
    }

    @Override
    protected Team[] getLosers() {
        Preconditions.checkNotNull(winner, "winner");
        return new Team[]{getOpponent(winner)};
    }

    @Override
    protected void createInventorySnapshots() {
        for (PlayerSession session : teamA.getOnlinePlayers()) {
            if (teamA.isAlive(session)) createInventorySnapshot(session);
        }

        for (PlayerSession session : teamB.getOnlinePlayers()) {
            if (teamB.isAlive(session)) createInventorySnapshot(session);
        }
    }

    @Override
    protected int getPlayersCount() {
        return teamA.size() + teamB.size();
    }

    @Override
    protected List<PlayerSession> getOnlinePlayers() {
        List<PlayerSession> players = Lists.newArrayList();
        players.addAll(teamA.getOnlinePlayers());
        players.addAll(teamB.getOnlinePlayers());
        return players;
    }

    @Override
    public List<String> getBoard(PlayerSession player) {
        return Lists.newArrayList();
    }
}
