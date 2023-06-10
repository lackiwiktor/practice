package me.ponktacology.practice.board;

import me.ponktacology.practice.Practice;
import me.ponktacology.practice.player.PlayerService;
import me.ponktacology.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class ScoreboardListener implements Listener {

    private final ScoreboardService scoreboardService;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        PracticePlayer practicePlayer = Practice.getService(PlayerService.class).get(event.getPlayer());
        scoreboardService.create(practicePlayer);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PracticePlayer practicePlayer = Practice.getService(PlayerService.class).get(event.getPlayer());
        scoreboardService.delete(practicePlayer);
    }
}
