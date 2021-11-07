package country.pvp.practice.queue;

import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.team.Team;
import country.pvp.practice.time.TimeUtil;
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

    public int getEloRangeFactor() {
        return (int) (TimeUtil.elapsed(joinTimeStamp) / 1000L) * 5;
    }

    public boolean hasPlayer(PracticePlayer player) {
        return team.hasPlayer(player);
    }
}
