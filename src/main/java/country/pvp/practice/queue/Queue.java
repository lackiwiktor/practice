package country.pvp.practice.queue;

import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.team.Team;
import lombok.Data;

import java.util.Comparator;
import java.util.PriorityQueue;

@Data
public class Queue<V extends Team> {

    private final Ladder ladder;
    private final MatchType type;
    private final PriorityQueue<QueueData<V>> queueData = new PriorityQueue<>(Comparator.naturalOrder());

    public QueueData<V> add(V team) {
        QueueData<V> data = new QueueData<>(team);
        queueData.add(data);
        return data;
    }

    public int size() {
        return queueData.size();
    }

    public void tick() {
        if (queueData.size() < 2) return;

        QueueData<V> firstTeam = queueData.poll();
        QueueData<V> secondTeam = queueData.poll();

        prepareMatch(firstTeam.getTeam(), secondTeam.getTeam());
    }

    public void prepareMatch(V firsTeam, V secondTeam) {
        //TODO: Create match
    }
}
