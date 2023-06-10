package me.ponktacology.practice.queue;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.ponktacology.practice.Cache;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.Service;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.queue.command.QueueCommands;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.ladder.LadderService;
import me.ponktacology.practice.queue.listener.QueueListener;
import me.ponktacology.practice.util.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class QueueService extends Service implements Cache {

  private final Map<Boolean, List<Queue>> queues = Maps.newHashMap();
  private final Map<PracticePlayer, Queue> playerToQueueMap = Maps.newHashMap();

  @Override
  public void configure() {
    addListener(new QueueListener(this));
    addCommand(new QueueCommands(this));

    for (Ladder ladder : Practice.getService(LadderService.class).getAllLadders()) {
      Logger.log("Created queue for %s ladder.", ladder.getName());

      createQueue(ladder);
    }

    Runnable queueTicker =
        () -> {
          for (List<Queue> queueList : queues.values()) {
            for (Queue queue : queueList) {
              queue.tick();
            }
          }
        };

    registerTask(queueTicker, 500L, TimeUnit.MILLISECONDS, false);
  }

  public void updatePlayerQueue(PracticePlayer player, @Nullable Queue queue) {
    if (queue == null) {
      playerToQueueMap.remove(player);
      return;
    }

    playerToQueueMap.put(player, queue);
  }

  public boolean isInQueue(PracticePlayer player) {
    return playerToQueueMap.containsKey(player);
  }

  public Queue getPlayerQueue(PracticePlayer player) {
    Preconditions.checkArgument(isInQueue(player), "player is not in a queue");
    return playerToQueueMap.get(player);
  }

  public List<Queue> getQueues(boolean ranked) {
    return queues.getOrDefault(ranked, Collections.emptyList());
  }

  public void createQueue(Ladder ladder) {
    queues.computeIfAbsent(false, (k) -> Lists.newArrayList()).add(new Queue(ladder, false));
    if (ladder.isRanked())
      queues.computeIfAbsent(true, (k) -> Lists.newArrayList()).add(new Queue(ladder, true));
  }

  @Override
  public int getSize() {
    return playerToQueueMap.size();
  }
}
