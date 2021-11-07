package country.pvp.practice.queue;

import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.player.PlayerState;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.team.Team;
import lombok.Data;

import java.util.Comparator;
import java.util.PriorityQueue;

@Data
public class Queue {

    private final Ladder ladder;
    private final MatchType type;
    private final PriorityQueue<QueueData<Team>> queueData = new PriorityQueue<>(Comparator.naturalOrder());

    public void add(Team team) {
        QueueData<Team> data = new QueueData<>(team);
        team.setPlayersState(PlayerState.QUEUING);
        queueData.add(data);
    }

    public void remove(Team team) {
        queueData.removeIf(it -> it.getTeam().equals(team));
    }

    public void remove(PracticePlayer player) {
        queueData.removeIf(it -> it.hasPlayer(player));
    }

    public QueueData<Team> get(PracticePlayer player) {
        return queueData.stream().filter(it -> it.hasPlayer(player)).findFirst().orElse(null);
    }

    public boolean hasPlayer(PracticePlayer player) {
        return queueData.stream().anyMatch(it -> it.hasPlayer(player));
    }

    public int size() {
        return queueData.size();
    }

    public void tick() {
        if (queueData.size() < 2) return;

        QueueData<Team> firstTeam = queueData.poll();
        QueueData<Team> secondTeam = queueData.poll();

        prepareMatch(firstTeam.getTeam(), secondTeam.getTeam());
    }

    public void prepareMatch(Team firsTeam, Team secondTeam) {
        //TODO: Create match
    }
}
