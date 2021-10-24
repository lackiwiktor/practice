package country.pvp.practice.queue;

import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.team.Team;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.PriorityQueue;

@RequiredArgsConstructor
public class Queue<V extends Team> {

    private final Ladder ladder;
    private final boolean ranked;
    private final PriorityQueue<QueueData<V>> queueData = new PriorityQueue<>(Comparator.naturalOrder());

    public void add(V team) {
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
