package country.pvp.practice.queue;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.team.PlayerTeam;

public class QueueManager {

    private final Table<Ladder, Boolean, Queue<PlayerTeam>> playerQueues = HashBasedTable.create();

    public void initPlayerQueue(Ladder ladder, boolean ranked) {
        if (ranked)
            playerQueues.put(ladder, true, new Queue<>(ladder, true));
        playerQueues.put(ladder, false, new Queue<>(ladder, false));
    }

    public Queue<PlayerTeam> getPlayerQueue(Ladder ladder, boolean ranked) {
        return playerQueues.get(ladder, ranked);
    }

    public void tick() {
        playerQueues.cellSet().forEach(cell -> cell.getValue().tick());
    }
}
