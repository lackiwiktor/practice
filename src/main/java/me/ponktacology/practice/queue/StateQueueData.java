package me.ponktacology.practice.queue;

import com.google.common.base.Preconditions;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.util.message.Recipient;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.data.StateData;
import me.ponktacology.practice.util.TimeUtil;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;

@Data
public class StateQueueData implements Comparable<StateQueueData>, StateData, Recipient {

  private final PracticePlayer player;
  private final Queue queue;
  private final Instant joinedAt = Instant.now();

  public int getEloRangeFactor() {
    Preconditions.checkArgument(isRanked());
    return (int) (TimeUtil.elapsed(joinedAt.getEpochSecond()) + 1) * 5;
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

  public boolean isWithinEloRange(StateQueueData other) {
    Preconditions.checkArgument(isRanked());
    int elo = other.getElo();
    int eloRangeFactor = getEloRangeFactor();
    return Math.max(1000 - eloRangeFactor, 0) <= elo && 1000 + eloRangeFactor >= elo;
  }

  private int getElo() {
    Preconditions.checkArgument(isRanked());
    return player.getElo(getLadder());
  }

  @Override
  public int compareTo(StateQueueData queueData) {
    if (isRanked()) return getElo() - queueData.getElo();
    return joinedAt.compareTo(queueData.joinedAt);
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
    StateQueueData queueData = (StateQueueData) o;
    return Objects.equals(player, queueData.player);
  }

  @Override
  public int hashCode() {
    return Objects.hash(player);
  }
}
