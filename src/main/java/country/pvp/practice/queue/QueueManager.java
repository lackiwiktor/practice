package country.pvp.practice.queue;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.team.Team;

import java.util.List;
import java.util.stream.Collectors;

public class QueueManager {

    private final Table<Ladder, MatchType, Queue> queues = HashBasedTable.create();

    public void initSoloQueue(Ladder ladder, MatchType... types) {
        for (MatchType type : types) {
            queues.put(ladder, type, new Queue(ladder, type));
        }
    }

    public Queue getSoloQueue(Ladder ladder, MatchType type) {
        return queues.get(ladder, type);
    }

    public void remove(PracticePlayer player) {
        getQueue(player).remove(player);
    }

    public Queue getQueue(PracticePlayer player) {
        return queues.values().stream().filter(it -> it.hasPlayer(player)).findFirst().orElse(null);
    }

    public QueueData<Team> getQueueData(PracticePlayer player) {
        Queue queue = getQueue(player);

        return queue.get(player);
    }

    public List<Queue> getSoloQueues(MatchType type) {
        return queues.cellSet()
                .stream()
                .filter(it -> it.getColumnKey() == type)
                .map(Table.Cell::getValue).collect(Collectors.toList());
    }

    public void tick() {
        queues.cellSet().forEach(cell -> cell.getValue().tick());
    }
}
