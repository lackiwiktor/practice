package country.pvp.practice.queue;

import com.google.common.base.Preconditions;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.message.Recipient;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.player.data.SessionData;
import country.pvp.practice.time.TimeUtil;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Data
public class SessionQueueData implements Comparable<SessionQueueData>, SessionData, Recipient {

    private final PlayerSession player;
    private final Queue queue;
    private final long joinTimeStamp = System.currentTimeMillis();

    private int getPlayerRank() {
        Preconditions.checkNotNull(isRanked());
        return player.getElo(queue.getLadder());
    }

    public int getEloRangeFactor() {
        Preconditions.checkNotNull(isRanked());
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

    public boolean isWithinEloRange(SessionQueueData other) {
        Preconditions.checkNotNull(isRanked());
        int elo = other.getElo();
        int eloRangeFactor = getEloRangeFactor();
        return Math.max(1000 - eloRangeFactor, 0) <= elo && 1000 + eloRangeFactor >= elo;
    }

    private int getElo() {
        Preconditions.checkNotNull(isRanked());
        return player.getElo(getLadder());
    }

    @Override
    public int compareTo(SessionQueueData queueData) {
        if (isRanked()) return getPlayerRank() - queueData.getPlayerRank();
        return (int) (joinTimeStamp - queueData.joinTimeStamp);
    }

    @Override
    public void receive(String message) {
        player.receive(message);
    }

    public String getName() {
        return player.getName();
    }

    public void removeFromQueue(boolean leftQueue) {
        queue.removePlayer(player, leftQueue);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionQueueData queueData = (SessionQueueData) o;
        return Objects.equals(player, queueData.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player);
    }
}
