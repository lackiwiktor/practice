package country.pvp.practice.queue;

import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.message.Recipient;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.player.data.PlayerData;
import country.pvp.practice.time.TimeUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Data
public class PlayerQueueData implements Comparable<PlayerQueueData>, PlayerData, Recipient {

    private final PracticePlayer player;
    private final Queue queue;
    private final long joinTimeStamp = System.currentTimeMillis();

    private int getPlayerRank() {
        return player.getElo(queue.getLadder());
    }

    public int getEloRangeFactor() {
        return (int) ((TimeUtil.elapsed(joinTimeStamp) / 1000L) + 1) * 5;
    }

    public boolean isRanked() {
        return queue.isRanked();
    }

    public String getLadderDisplayName() {
        return queue.getLadder().getDisplayName();
    }

    public Ladder getLadder() {
        return queue.getLadder();
    }

    @Override
    public int compareTo( PlayerQueueData queueData) {
        if (queue.isRanked()) return getPlayerRank() - queueData.getPlayerRank();
        return (int) (joinTimeStamp - queueData.joinTimeStamp);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerQueueData queueData = (PlayerQueueData) o;
        return Objects.equals(player, queueData.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player);
    }

    @Override
    public void receive(String message) {
        player.receive(message);
    }

    public String getName() {
        return player.getName();
    }
}
