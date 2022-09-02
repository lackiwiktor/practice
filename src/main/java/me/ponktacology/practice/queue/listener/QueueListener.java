package me.ponktacology.practice.queue.listener;

import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.PracticePlayerListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class QueueListener extends PracticePlayerListener {

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) {
        PracticePlayer practicePlayer = get(event);

        if (practicePlayer.isInQueue()) {
            practicePlayer.removeFromQueue();
        }
    }
}
