package me.ponktacology.practice.queue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import me.ponktacology.practice.Messages;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.hotbar.HotBarService;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.match.MatchService;
import me.ponktacology.practice.match.team.type.SoloTeam;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.data.PlayerState;
import me.ponktacology.practice.util.message.MessagePattern;
import me.ponktacology.practice.util.message.Messenger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
@Getter
public class Queue {

  private final Set<StateQueueData> entries = Sets.newHashSet();
  private final Ladder ladder;
  private final boolean ranked;

  public void addPlayer(PracticePlayer player) {
    StateQueueData entry = new StateQueueData(player, this);
    entries.add(entry);

    player.setState(PlayerState.QUEUING, entry);

    Practice.getService(HotBarService.class).apply(player);

    Messenger.message(
        player,
        Messages.PLAYER_JOINED_QUEUE.match(
            new MessagePattern("{queue}", ladder.getDisplayName()),
            new MessagePattern("{ranked}", ranked ? "&branked" : "&dunranked")));
  }

  public void removePlayer(PracticePlayer player, boolean leftQueue) {
    entries.removeIf(it -> it.getPlayer().equals(player));

    if (leftQueue) {
      Messenger.message(player, Messages.PLAYER_LEFT_QUEUE);
      player.setState(PlayerState.IN_LOBBY);

      Practice.getService(HotBarService.class).apply(player);
    }
  }

  public void tick() {
    if (entries.size() < 2) return;
    Set<StateQueueData> toRemove = Sets.newHashSet();

    for (StateQueueData entry : entries) {
      for (StateQueueData other : entries) {
        if (entry.equals(other) || toRemove.contains(entry) || toRemove.contains(other)) continue;
        if (ranked && !entry.isWithinEloRange(other)) continue;

        Messenger.messageSuccess(
            entry, Messages.QUEUE_FOUND_OPPONENT.match("{player}", other.getName()));
        Messenger.messageSuccess(
            other, Messages.QUEUE_FOUND_OPPONENT.match("{player}", entry.getName()));

        startMatch(entry, other);
        toRemove.addAll(ImmutableList.of(entry, other));
        return;
      }
    }

    entries.removeAll(toRemove);
  }

  public int size() {
    return entries.size();
  }

  private void startMatch(StateQueueData queueData1, StateQueueData queueData2) {
    Practice.getService(MatchService.class)
        .start(
            ladder,
            ranked,
            false,
            SoloTeam.of(queueData1.getPlayer()),
            SoloTeam.of(queueData2.getPlayer()));
  }
}
