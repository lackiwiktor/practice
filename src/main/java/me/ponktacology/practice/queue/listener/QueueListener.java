package me.ponktacology.practice.queue.listener;

import lombok.RequiredArgsConstructor;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.PracticePlayerListener;
import me.ponktacology.practice.queue.Queue;
import me.ponktacology.practice.queue.QueueService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class QueueListener extends PracticePlayerListener {

    private final QueueService queueService;

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) {
        PracticePlayer practicePlayer = get(event);

        if (queueService.isInQueue(practicePlayer)) {
            Queue queue = queueService.getPlayerQueue(practicePlayer);
            queue.removePlayer(practicePlayer, false);
        }
    }
}
