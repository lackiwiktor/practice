package country.pvp.practice.match;

import com.google.common.collect.Sets;
import country.pvp.practice.PracticePlugin;
import country.pvp.practice.arena.Arena;
import country.pvp.practice.concurrent.TaskDispatcher;
import country.pvp.practice.itembar.ItemBarManager;
import country.pvp.practice.itembar.ItemBarType;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.message.MessagePattern;
import country.pvp.practice.message.Messager;
import country.pvp.practice.message.Messages;
import country.pvp.practice.message.Recipient;
import country.pvp.practice.player.PlayerUtil;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.player.data.PlayerState;
import country.pvp.practice.team.Team;
import country.pvp.practice.visibility.VisibilityUpdater;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class Match implements Recipient {

    private final VisibilityUpdater visibilityUpdater;
    private final LobbyService lobbyService;
    private final MatchManager matchManager;
    private final ItemBarManager itemBarManager;

    private final UUID id = UUID.randomUUID(); //match-id
    private final Ladder ladder;
    private final Arena arena;
    private final Team teamA;
    private final Team teamB;
    private final boolean ranked;
    private final Set<PracticePlayer> spectators = Sets.newHashSet();

    private Team winner;

    private MatchState state = MatchState.COUNTDOWN;
    private BukkitRunnable countDownRunnable;

    public void start() {
        matchManager.add(this);
        prepareTeams();
    }

    void prepareTeams() {
        prepareTeam(teamA, arena.getSpawnLocation1());
        prepareTeam(teamB, arena.getSpawnLocation2());
        updateVisibility();
        startCountDown();
    }

    void prepareTeam(Team team, Location spawnLocation) {
        team.setMatchData(this);
        team.setPlayersState(PlayerState.IN_MATCH);
        team.teleport(spawnLocation);
        team.resetPlayers();
        team.giveKits(ladder);
    }

    void updateVisibility() {
        for (PracticePlayer playerA : teamA.getOnlinePlayers()) {
            for (PracticePlayer playerB : teamB.getOnlinePlayers()) {
                visibilityUpdater.update(playerA, playerB);
                visibilityUpdater.update(playerB, playerA);
            }
        }
    }

    void startCountDown() {
        AtomicInteger count = new AtomicInteger(6);
        (countDownRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (count.decrementAndGet() > 0) {
                    broadcast(Messages.MATCH_COUNTDOWN.match("{time}", count.get()));
                } else {
                    state = MatchState.FIGHT;
                    broadcast(Messages.MATCH_START);
                    cancel();
                }
            }
        }).runTaskTimer(PracticePlugin.getPlugin(PracticePlugin.class), 20L, 20L);
    }

    void broadcast(String message) {
        Messager.message(this, message);
    }

    void broadcast(Messages message) {
        Messager.message(this, message);
    }

    void broadcast(Team team, Messages message) {
        Messager.message(team, message);
    }

    void broadcast(Team team, String message) {
        Messager.message(team, message);
    }

    void cancelCountDown() {
        if (countDownRunnable == null) return;
        countDownRunnable.cancel();
    }

    public Team getOpponent(Team team) {
        return team.equals(teamA) ? teamB : teamA;
    }

    public Team getOpponent(PracticePlayer player) {
        return teamA.hasPlayer(player) ? teamB : teamA;
    }

    public Team getTeam(PracticePlayer player) {
        return teamA.hasPlayer(player) ? teamA : teamB;
    }

    public boolean isInMatch(PracticePlayer player) {
        return teamA.hasPlayer(player) || teamB.hasPlayer(player);
    }

    public boolean isAlive(PracticePlayer player) {
        return getTeam(player).isAlive(player);
    }

    public void handleDeath(PracticePlayer player) {
        PlayerMatchData matchData = player.getStateData();
        matchData.setDead(true);

        if (matchData.getLastAttacker() != null) {
            PracticePlayer killer = matchData.getLastAttacker();

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

        updateVisibility();
        PlayerUtil.resetPlayer(player.getPlayer());
        player.respawn();
        player.setVelocity(new Vector());
        player.teleport(player.getLocation().add(0, 3, 0));
    }

    public void handleRespawn(PracticePlayer player) {
        Team team = getTeam(player);

        if (team.isDead()) {
            end(getOpponent(team));
            return;
        }

        setupSpectator(player);
    }

    public void handleDisconnect(PracticePlayer player) {
        broadcast(teamA, Messages.MATCH_PLAYER_DISCONNECT.match("{player}", getFormattedDisplayName(player, teamA)));
        broadcast(teamB, Messages.MATCH_PLAYER_DISCONNECT.match("{player}", getFormattedDisplayName(player, teamB)));

        PlayerMatchData matchData = player.getStateData();

        matchData.setDead(true);
        matchData.setDisconnected(true);

        Team team = getTeam(player);

        if (team.isDead()) {
            end(getOpponent(team));
        }
    }

    void setupSpectator(PracticePlayer player) {
        player.enableFlying();
    }

    void end(Team winner) {
        state = MatchState.END;
        this.winner = winner;
        cancelCountDown();

        if (winner != null) {
            broadcast(winner, Messages.MATCH_WON);
            broadcast(getOpponent(winner), Messages.MATCH_LOST);
        }

        Runnable runnable = () -> {
            for (PracticePlayer player : teamA.getOnlinePlayers()) {
                lobbyService.moveToLobby(player);
            }

            for (PracticePlayer player : teamB.getOnlinePlayers()) {
                lobbyService.moveToLobby(player);
            }

            matchManager.remove(this);
        };

        TaskDispatcher.runLater(runnable, 3L, TimeUnit.SECONDS);
    }

    public void cancel(String reason) {
        broadcast(Messages.MATCH_CANCELLED.match("{reason}", reason));
        end(null);
    }

    public String getFormattedDisplayName(PracticePlayer player, Team team) {
        return (team.hasPlayer(player) ? ChatColor.GREEN : ChatColor.RED) + player.getName();
    }

    public String getFormattedDisplayName(PracticePlayer player, PracticePlayer other) {
        return getFormattedDisplayName(player, getTeam(other));
    }

    public void startSpectating(PracticePlayer spectator, PracticePlayer player) {
        spectators.add(spectator);
        itemBarManager.apply(ItemBarType.SPECTATOR, spectator);
        broadcast("&ePlayer &f" + spectator.getName() + "&e started spectating this match.");
        spectator.teleport(player.getLocation());
        setupSpectator(spectator);
        spectator.setState(PlayerState.SPECTATING, new PlayerSpectatingData(this));

        for (PracticePlayer playerA : teamA.getOnlinePlayers()) {
            visibilityUpdater.update(spectator, playerA);
            visibilityUpdater.update(playerA, spectator);
        }

        for (PracticePlayer playerB : teamB.getOnlinePlayers()) {
            visibilityUpdater.update(spectator, playerB);
            visibilityUpdater.update(playerB, spectator);
        }
    }

    public void stopSpectating(PracticePlayer spectator) {
        spectators.remove(spectator);
        lobbyService.moveToLobby(spectator);
    }

    @Override
    public void receive(String message) {
        teamA.receive(message);
        teamB.receive(message);
    }

    public int getPlayersCount() {
        return teamA.size() + teamB.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return Objects.equals(id, match.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
