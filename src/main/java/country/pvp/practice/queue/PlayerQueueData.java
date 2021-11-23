package country.pvp.practice.queue;

import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.player.data.PlayerData;
import country.pvp.practice.time.TimeUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Data
public class PlayerQueueData implements Comparable<PlayerQueueData>, PlayerData {

    private final PracticePlayer player;
    private final Queue queue;
    private final long joinTimeStamp = System.currentTimeMillis();

    private int getPlayerRank() {
        return player.getRank(queue.getLadder());
    }

    public int getEloRangeFactor() {
        return (int) (TimeUtil.elapsed(joinTimeStamp) / 1000L) * 5;
    }

    public boolean isRanked() {
        return queue.isRanked();
    }

    public String getLadderDisplayName() {
        return queue.getLadder().getDisplayName();
    }

    @Override
    public int compareTo(@NotNull PlayerQueueData queueData) {
        if (queue.isRanked()) return getPlayerRank() - queueData.getPlayerRank();
        return (int) (joinTimeStamp - queueData.joinTimeStamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerQueueData queueData = (PlayerQueueData) o;
        return Objects.equals(player, queueData.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player);
    }
}
