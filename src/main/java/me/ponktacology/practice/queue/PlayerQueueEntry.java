package me.ponktacology.practice.queue;

import lombok.Data;
import me.ponktacology.practice.player.PracticePlayer;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;

@Data
public class PlayerQueueEntry {

  private final PracticePlayer player;
  private final Instant joinedAt = Instant.now();

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PlayerQueueEntry queueData = (PlayerQueueEntry) o;
    return Objects.equals(player, queueData.player);
  }

  @Override
  public int hashCode() {
    return Objects.hash(player);
  }
}
