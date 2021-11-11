package country.pvp.practice.match;

import country.pvp.practice.PracticePlugin;
import country.pvp.practice.arena.Arena;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.message.Messager;
import country.pvp.practice.player.PlayerState;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.team.Team;
import country.pvp.practice.visibility.VisibilityUpdater;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class Match {

    private final VisibilityUpdater visibilityUpdater;

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

    private void prepareTeams() {
        prepareTeam(teamA, arena.getSpawnLocation1());
        prepareTeam(teamB, arena.getSpawnLocation2());

        for (PracticePlayer playerA : teamA.getPlayers()) {
            for (PracticePlayer playerB : teamB.getPlayers()) {
                visibilityUpdater.update(playerA, playerB);
            }
        }

        startCountDown();
    }

    private void prepareTeam(Team team, Location spawnLocation) {
        team.setMatchData(this);
        team.setPlayersState(PlayerState.IN_MATCH);
        team.teleport(spawnLocation);
        team.resetPlayers();
        team.giveKits(ladder);
    }

    public void startCountDown() {
        AtomicInteger count = new AtomicInteger(6);
        (countDownRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (count.decrementAndGet() > 0) {
                    broadcastMessage("Match will start in " + count.get() + " seconds.");
                } else if (count.get() == 0) {
                    state = MatchState.FIGHT;
                    broadcastMessage("Match has started!");
                    cancel();
                }
            }
        }).runTaskTimer(PracticePlugin.getPlugin(PracticePlugin.class), 20L, 20L);
    }

    public void cancelCountDown() {
        if (countDownRunnable == null) return;
        if (Bukkit.getScheduler().isCurrentlyRunning(countDownRunnable.getTaskId())) {
            countDownRunnable.cancel();
        }
    }

    private void broadcastMessage(String message) {
        Messager.message(teamA, message);
        Messager.message(teamB, message);
    }

    public Team getOpponent(Team team) {
        return team.equals(teamA) ? teamB : teamA;
    }

    public boolean isInMatch(PracticePlayer player) {
        return teamA.hasPlayer(player) || teamB.hasPlayer(player);
    }
}
