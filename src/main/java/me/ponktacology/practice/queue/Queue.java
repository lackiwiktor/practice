package me.ponktacology.practice.queue;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.ponktacology.practice.Messages;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.hotbar.HotBarService;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.match.MatchService;
import me.ponktacology.practice.match.team.type.SoloTeam;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.data.PlayerState;
import me.ponktacology.practice.util.TimeUtil;
import me.ponktacology.practice.util.message.MessagePattern;
import me.ponktacology.practice.util.message.Messenger;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@RequiredArgsConstructor
@Getter
public class Queue {

  private final Map<PracticePlayer, PlayerQueueEntry> entries = Maps.newConcurrentMap();
  private final Ladder ladder;
  private final boolean ranked;

  public void addPlayer(PracticePlayer player) {
    PlayerQueueEntry entry = new PlayerQueueEntry(player);

    Practice.getService(QueueService.class).updatePlayerQueue(player, this);
    entries.put(player, entry);

    player.setState(PlayerState.QUEUING);

    Practice.getService(HotBarService.class).apply(player);

    Messenger.message(
        player,
        Messages.PLAYER_JOINED_QUEUE.match(
            new MessagePattern("{queue}", ladder.getDisplayName()),
            new MessagePattern("{ranked}", ranked ? "&branked" : "&dunranked")));
  }

  public void removePlayer(PracticePlayer player, boolean leftQueue) {
    if (leftQueue) {
      Messenger.message(player, Messages.PLAYER_LEFT_QUEUE);
      player.setState(PlayerState.IN_LOBBY);

      Practice.getService(HotBarService.class).apply(player);
    }

    entries.remove(player);
    Practice.getService(QueueService.class).updatePlayerQueue(player, null);
  }

  public @Nullable PlayerQueueEntry getEntry(PracticePlayer player) {
    return entries.get(player);
  }

  public void tick() {
    if (entries.size() < 2) return;

    for (PlayerQueueEntry entry : entries.values()) {
      for (PlayerQueueEntry otherEntry : entries.values()) {
        if (entry.equals(otherEntry) || (ranked && !isWithinEloRange(entry, otherEntry))) continue;
        PracticePlayer player = entry.getPlayer();
        PracticePlayer other = otherEntry.getPlayer();

        if (player.getPingFactor() > 0 && other.getPingFactor() > 0) {
          int pingDiff = Math.abs(player.getPing() - other.getPingFactor());

          if (pingDiff > player.getPingFactor() || pingDiff > other.getPingFactor()) continue;
        }

        Messenger.messageSuccess(
            entry.getPlayer(),
            Messages.QUEUE_FOUND_OPPONENT.match("{player}", otherEntry.getPlayer().getName()));
        Messenger.messageSuccess(
            otherEntry.getPlayer(),
            Messages.QUEUE_FOUND_OPPONENT.match("{player}", otherEntry.getPlayer().getName()));

        removePlayer(entry.getPlayer(), false);
        removePlayer(otherEntry.getPlayer(), false);

        startMatch(entry, otherEntry);
        return;
      }
    }
  }

  public int size() {
    return entries.size();
  }

  private boolean startMatch(PlayerQueueEntry entry, PlayerQueueEntry otherEntry) {
    return Practice.getService(MatchService.class)
            .start(
                ladder,
                null,
                ranked,
                false,
                SoloTeam.of(entry.getPlayer()),
                SoloTeam.of(otherEntry.getPlayer()))
        != null;
  }

  public int getEloRangeFactor(PlayerQueueEntry entry) {
    return (int) Math.max(TimeUtil.elapsed(entry.getJoinedAt()) / 1000L, 1) * 5;
  }

  private boolean isWithinEloRange(PlayerQueueEntry entry, PlayerQueueEntry other) {
    int elo = entry.getPlayer().getElo(ladder);
    int eloRangeFactor = getEloRangeFactor(other);
    return Math.max(1000 - eloRangeFactor, 0) <= elo && 1000 + eloRangeFactor >= elo;
  }
}
