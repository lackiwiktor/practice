package country.pvp.practice.match;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import country.pvp.practice.arena.Arena;
import country.pvp.practice.itembar.ItemBarService;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.match.elo.EloUtil;
import country.pvp.practice.match.snapshot.InventorySnapshot;
import country.pvp.practice.match.snapshot.InventorySnapshotManager;
import country.pvp.practice.match.team.SoloTeam;
import country.pvp.practice.match.team.Team;
import country.pvp.practice.message.MessagePattern;
import country.pvp.practice.message.Messages;
import country.pvp.practice.player.PlayerService;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.visibility.VisibilityUpdater;

import java.util.List;


public class StandardMatch extends Match {

    private final PlayerService playerService;
    private final Team teamA;
    private final Team teamB;

    StandardMatch(MatchManager matchManager, VisibilityUpdater visibilityUpdater, LobbyService lobbyService, ItemBarService itemBarService, Arena arena, Ladder ladder, boolean ranked, boolean duel, InventorySnapshotManager snapshotManager, PlayerService playerService, Team teamA, Team teamB) {
        super(snapshotManager, matchManager, visibilityUpdater, lobbyService, itemBarService, arena, ladder, ranked, duel);
        this.playerService = playerService;
        this.teamA = teamA;
        this.teamB = teamB;
    }

    @Override
    void prepareTeams() {
        prepareTeam(teamA, arena.getSpawnLocation1());
        prepareTeam(teamB, arena.getSpawnLocation2());
        updateTeamVisibility();
    }

    @Override
    void handleEnd() {
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
    void movePlayersToLobby() {
        for (PlayerSession session : teamA.getOnlinePlayers()) {
            lobbyService.moveToLobby(session);
        }

        for (PlayerSession session : teamB.getOnlinePlayers()) {
            lobbyService.moveToLobby(session);
        }
    }

    @Override
    void broadcastPlayerDeath(PlayerSession player) {
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
    void handleRespawn(PlayerSession player) {
        Team team = getTeam(player);

        if (team.isDead()) {
            end(getOpponent(team));
        }
    }

    @Override
    public void handleDisconnect(PlayerSession player) {
        createInventorySnapshot(player);
        broadcast(teamA, Messages.MATCH_PLAYER_DISCONNECTED.match("{player}", getFormattedDisplayName(player, teamA)));
        broadcast(teamB, Messages.MATCH_PLAYER_DISCONNECTED.match("{player}", getFormattedDisplayName(player, teamB)));
        player.handleDisconnectInMatch();
        country.pvp.practice.match.team.Team team = getTeam(player);

        if (team.isDead() && state != MatchState.END) {
            end(getOpponent(team));
        }
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
    List<PlayerSession> getOnlinePlayers() {
        List<PlayerSession> players = super.getOnlinePlayers();
        players.addAll(teamA.getOnlinePlayers());
        players.addAll(teamB.getOnlinePlayers());
        return players;
    }

    @Override
    InventorySnapshot createInventorySnapshot(PlayerSession player) {
        InventorySnapshot snapshot = super.createInventorySnapshot(player);

        Team team = getTeam(player);
        Team opponent = getOpponent(team);

        if (opponent instanceof SoloTeam) {
            SoloTeam soloOpponent = (SoloTeam) opponent;
            PlayerSession opponentPlayer = soloOpponent.getPlayerSession();
            snapshot.setOpponent(opponentPlayer.getUuid());
        }

        return snapshot;
    }

    @Override
    Team[] getLosers() {
        Preconditions.checkNotNull(winner, "winner");
        return new Team[]{getOpponent(winner)};
    }

    @Override
    public boolean isOnSameTeam(PlayerSession player, PlayerSession other) {
        return getTeam(player).hasPlayer(other);
    }

    @Override
    void createInventorySnapshots() {
        for (PlayerSession session : teamA.getOnlinePlayers()) {
            if (teamA.isAlive(session)) createInventorySnapshot(session);
        }

        for (PlayerSession session : teamB.getOnlinePlayers()) {
            if (teamB.isAlive(session)) createInventorySnapshot(session);
        }
    }

    @Override
    public boolean isInMatch(PlayerSession player) {
        return teamA.hasPlayer(player) || teamB.hasPlayer(player);
    }

    @Override
    public boolean isAlive(PlayerSession player) {
        return getTeam(player).isAlive(player);
    }

    @Override
    int getPlayersCount() {
        return teamA.size() + teamB.size();
    }

    @Override
    public List<String> getBoard(PlayerSession player) {
        return Lists.newArrayList();
    }
}
