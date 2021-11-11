package country.pvp.practice.match;

import country.pvp.practice.PracticePlugin;
import country.pvp.practice.arena.Arena;
import country.pvp.practice.concurrent.TaskDispatcher;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.message.Recipient;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.player.data.PlayerState;
import country.pvp.practice.team.Team;
import country.pvp.practice.visibility.VisibilityUpdater;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class Match implements Recipient {

    private final VisibilityUpdater visibilityUpdater;
    private final LobbyService lobbyService;

    private final UUID id = UUID.randomUUID(); //match-id
    private final Ladder ladder;
    private final Arena arena;
    private final Team teamA;
    private final Team teamB;
    private final boolean ranked;

    private MatchState state = MatchState.COUNTDOWN;
    private BukkitRunnable countDownRunnable;

    public void startMatch() {
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
                    //   broadcastMessage("Match will start in " + count.get() + " seconds.");
                } else if (count.get() == 0) {
                    state = MatchState.FIGHT;
                    //  broadcastMessage("Match has started!");
                    cancel();
                }
            }
        }).runTaskTimer(PracticePlugin.getPlugin(PracticePlugin.class), 20L, 20L);
    }

    void cancelCountDown() {
        if (countDownRunnable == null) return;
        if (Bukkit.getScheduler().isCurrentlyRunning(countDownRunnable.getTaskId())) {
            countDownRunnable.cancel();
        }
    }

    Team getOpponent(Team team) {
        return team.equals(teamA) ? teamB : teamA;
    }

    Team getOpponent(PracticePlayer player) {
        return teamA.hasPlayer(player) ? teamB : teamA;
    }

    Team getTeam(PracticePlayer player) {
        return teamA.hasPlayer(player) ? teamA : teamB;
    }

    public boolean isInMatch(PracticePlayer player) {
        return teamA.hasPlayer(player) || teamB.hasPlayer(player);
    }

    public boolean isAlive(PracticePlayer player) {
        return getTeam(player).isAlive(player);
    }

    public void handleDeath(PracticePlayer player) {
        MatchData matchData = player.getStateData(PlayerState.IN_MATCH);
        matchData.setDead(true);
        updateVisibility();
        player.respawn();
        player.setVelocity(new Vector());
        player.teleport(player.getLocation().add(0, 3, 0));
    }

    public void handleRespawn(PracticePlayer player) {
        Team team = getTeam(player);

        if (!team.isAnyPlayerAlive()) {
            endMatch(getOpponent(team));
            return;
        }

        setupSpectator(player);
    }

    public void handleDisconnect(PracticePlayer player) {
        MatchData matchData = player.getStateData(PlayerState.IN_MATCH);

        matchData.setDead(true);
        matchData.setDisconnected(true);

        Team team = getTeam(player);

        if (!team.isAnyPlayerAlive()) {
            endMatch(getOpponent(team));
        }
    }

    void setupSpectator(PracticePlayer player) {
        player.enableFlying();
    }

    void endMatch(Team winner) {
        Runnable runnable = () -> {
            for (PracticePlayer player : teamA.getOnlinePlayers()) {
                lobbyService.moveToLobby(player);
            }

            for (PracticePlayer player : teamB.getOnlinePlayers()) {
                lobbyService.moveToLobby(player);
            }
        };

        TaskDispatcher.runLater(runnable, 3L, TimeUnit.SECONDS);
    }

    public String getFormattedDisplayName(PracticePlayer player, PracticePlayer other) {
        return (getTeam(player).hasPlayer(other) ? ChatColor.GREEN : ChatColor.RED) + player.getName();
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
