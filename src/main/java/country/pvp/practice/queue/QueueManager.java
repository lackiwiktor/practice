package country.pvp.practice.queue;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import country.pvp.practice.arena.ArenaManager;
import country.pvp.practice.itembar.ItemBarService;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.match.MatchProvider;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class QueueManager {

    private final Map<Boolean, List<Queue>> queues = Maps.newConcurrentMap();

    private final ItemBarService itemBarService;
    private final ArenaManager arenaManager;
    private final MatchProvider matchProvider;

    public void initQueue(Ladder ladder) {
        queues.computeIfAbsent(false, (k) -> Lists.newArrayList()).add(new Queue(itemBarService, arenaManager, matchProvider, ladder, false));
        if (ladder.isRanked())
            queues.computeIfAbsent(true, (k) -> Lists.newArrayList()).add(new Queue(itemBarService, arenaManager, matchProvider,  ladder, true));
    }

    public List<Queue> getQueues(boolean ranked) {
        return queues.get(ranked);
    }

    public int getPlayersInQueueCount() {
        return queues.values().stream().mapToInt(it -> it.stream().mapToInt(Queue::size).sum()).sum();
    }

    public void tick() {
        for (List<Queue> queueList : queues.values()) {
            for (Queue queue : queueList) {
                queue.tick();
            }
        }
    }
}
