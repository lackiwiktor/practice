package country.pvp.practice.queue;

import country.pvp.practice.team.Team;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class QueueData<V extends Team> implements Comparable<QueueData> {

    private final V team;
    private final long joinTimeStamp = System.currentTimeMillis();

    @Override
    public int compareTo(@NotNull QueueData playerQueueData) {
        return (int) (this.joinTimeStamp - playerQueueData.joinTimeStamp);
    }
}
