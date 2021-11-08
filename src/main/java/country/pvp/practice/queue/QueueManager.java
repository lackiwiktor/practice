package country.pvp.practice.queue;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import country.pvp.practice.arena.ArenaManager;
import country.pvp.practice.itembar.ItemBarManager;
import country.pvp.practice.ladder.Ladder;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class QueueManager {

    private final ItemBarManager itemBarManager;
    private final ArenaManager arenaManager;
    private final Map<Boolean, List<Queue>> queues = Maps.newHashMap();

    public void initQueue(Ladder ladder) {
        queues.computeIfAbsent(false, (k) -> Lists.newArrayList()).add(new Queue(ladder, false, itemBarManager, arenaManager));
        if (ladder.isRanked())
            queues.computeIfAbsent(true, (k) -> Lists.newArrayList()).add(new Queue(ladder, true, itemBarManager, arenaManager));
    }

    public List<Queue> getQueues(boolean ranked) {
        return queues.get(ranked);
    }

    public void tick() {
        for (List<Queue> queueList : queues.values()) {
            for (Queue queue : queueList) {
                queue.tick();
            }
        }
    }
}
