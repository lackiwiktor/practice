package me.ponktacology.practice.player.listener;

import lombok.RequiredArgsConstructor;
import me.ponktacology.practice.player.PlayerService;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.PracticePlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class PreparePlayerListener extends PracticePlayerListener {

  private final PlayerService partyDuelService;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void playerLoginEvent(AsyncPlayerPreLoginEvent event) {
    if (Bukkit.getPlayer(event.getUniqueId()) != null) {
      event.disallow(
          AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "You are already connected to this server.");
      return;
    }

    PracticePlayer practicePlayer = new PracticePlayer(event.getUniqueId(), event.getName());

    try {
      partyDuelService.create(practicePlayer);
    } catch (Exception e) {
      e.printStackTrace();
      event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Error");
    }
  }

  @EventHandler
  public void playerJoinEvent(PlayerJoinEvent event) {
    event.setJoinMessage(null);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void playerQuitEvent(PlayerQuitEvent event) {
    event.setQuitMessage(null);
    Player player = event.getPlayer();
    partyDuelService.delete(player);
  }
}
