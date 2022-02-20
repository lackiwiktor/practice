package country.pvp.practice.match.type;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import country.pvp.practice.Messages;
import country.pvp.practice.arena.DuplicatedArena;
import country.pvp.practice.itembar.ItemBarService;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.MatchManager;
import country.pvp.practice.match.MatchState;
import country.pvp.practice.match.RematchData;
import country.pvp.practice.match.elo.EloUtil;
import country.pvp.practice.match.snapshot.InventorySnapshot;
import country.pvp.practice.match.snapshot.InventorySnapshotManager;
import country.pvp.practice.match.team.Team;
import country.pvp.practice.match.team.type.SoloTeam;
import country.pvp.practice.player.PlayerRepository;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.util.message.FormatUtil;
import country.pvp.practice.util.message.MessagePattern;
import country.pvp.practice.visibility.VisibilityUpdater;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class TeamMatch extends Match {

    private final PlayerRepository playerRepository;
    @Getter
    private final Team teamA;
    @Getter
    private final Team teamB;

    public TeamMatch(MatchManager matchManager,
                     VisibilityUpdater visibilityUpdater,
                     LobbyService lobbyService,
                     ItemBarService itemBarService,
                     DuplicatedArena arena,
                     Ladder ladder,
                     boolean ranked,
                     boolean duel,
                     InventorySnapshotManager snapshotManager,
                     PlayerRepository playerRepository,
                     Team teamA,
                     Team teamB) {
        super(snapshotManager, matchManager, visibilityUpdater, lobbyService, itemBarService, arena, ladder, ranked, duel);
        this.playerRepository = playerRepository;
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

                playerRepository.saveAsync(winnerTeam.getPlayerSession());
                playerRepository.saveAsync(loserTeam.getPlayerSession());
            }

            PlayerSession playerSession = winnerTeam.getPlayerSession();
            PlayerSession opponentSession = loserTeam.getPlayerSession();

            playerSession.setRematchData(new RematchData(opponentSession, ladder));
            opponentSession.setRematchData(new RematchData(playerSession, ladder));
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
    public boolean canEndRound() {
        if (teamA.isDead()) {
            winner = teamA;
            return true;
        } else if (teamB.isDead()) {
            winner = teamB;
            return true;
        }

        return false;
    }

    public @Nullable Team getTeam(PlayerSession player) {
        if (teamA.hasPlayer(player)) {
            return teamA;
        } else if (teamB.hasPlayer(player)) {
            return teamB;
        }

        return null;
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
        for (PlayerSession session : teamA.getAlivePlayers()) {
            createInventorySnapshot(session);
        }

        for (PlayerSession session : teamB.getAlivePlayers()) {
            createInventorySnapshot(session);
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
        List<String> lines = Lists.newArrayList();

        if (spectators.contains(player)) {
            Team playerTeam = teamA;
            Team opponentTeam = teamB;

            String teamName = playerTeam.getName();
            String teamNameOpponent = opponentTeam.getName();

            if (playerTeam instanceof SoloTeam && opponentTeam instanceof SoloTeam) {
                SoloTeam soloTeam = (SoloTeam) playerTeam;
                SoloTeam soloTeamOpponent = (SoloTeam) opponentTeam;

                lines.add(ChatColor.GREEN + teamName + ChatColor.WHITE + " Ping: " + ChatColor.WHITE + soloTeam.getPing());
                lines.add(ChatColor.BLUE + teamNameOpponent + ChatColor.WHITE + " Ping: " + ChatColor.WHITE + soloTeamOpponent.getPing());
            } else {
                lines.add(ChatColor.GREEN + "Team " + teamName + ": " + ChatColor.WHITE + playerTeam.getAlivePlayersCount() + "/" + playerTeam.size());
                lines.add(ChatColor.BLUE + "Team " + teamNameOpponent + ": " + ChatColor.WHITE + opponentTeam.getAlivePlayersCount() + "/" + opponentTeam.size());
            }

            lines.add(" ");
        } else {
            Team playerTeam = getTeam(player);
            Team opponentTeam = getOpponent(playerTeam);

            if (playerTeam instanceof SoloTeam && opponentTeam instanceof SoloTeam) {
                if (state == MatchState.STARTING_ROUND) {
                    lines.add(ChatColor.WHITE + "Opponent: " + ChatColor.YELLOW + opponentTeam.getName());
                    lines.add(" ");
                }

                SoloTeam soloTeam = (SoloTeam) playerTeam;
                SoloTeam soloTeamOpponent = (SoloTeam) opponentTeam;

                lines.add(ChatColor.WHITE + "Your Ping: " + ChatColor.YELLOW + soloTeam.getPing());
                lines.add(ChatColor.WHITE + "Their Ping: " + ChatColor.YELLOW + soloTeamOpponent.getPing());
                lines.add(" ");
            } else if (getPlayersCount() > 5) {
                lines.add(ChatColor.GREEN + "Team: " + ChatColor.WHITE + playerTeam.getAlivePlayersCount() + "/" + playerTeam.size());
                lines.add(ChatColor.RED + "Opponents: " + ChatColor.WHITE + opponentTeam.getAlivePlayersCount() + "/" + opponentTeam.size());
                lines.add(" ");
            } else {
                lines.add(ChatColor.GREEN + "Team: " + ChatColor.WHITE + playerTeam.getAlivePlayersCount() + "/" + playerTeam.size());

                for (PlayerSession session : playerTeam.getPlayers()) {
                    boolean isOnline = session.isOnline();
                    String line = " "
                            + ((!isOnline || !isAlive(session)) ? (ChatColor.STRIKETHROUGH + ChatColor.GRAY.toString()) : "")
                            + ChatColor.DARK_GREEN + session.getName()
                            + (isOnline && isAlive(session) ? (ChatColor.WHITE + " " + FormatUtil.formatHealthWithHeart(session.getHealth())) : "");
                    lines.add(line);
                }

                lines.add(" ");
                lines.add(ChatColor.RED + "Opponents: " + ChatColor.WHITE + opponentTeam.getAlivePlayersCount() + "/" + opponentTeam.size());
            }
        }

        return lines;
    }
}
