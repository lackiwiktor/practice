package me.ponktacology.practice.player;

import me.ponktacology.practice.Practice;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;

@RequiredArgsConstructor
public class PracticePlayerListener implements Listener {

  public PracticePlayer get(Player player) {
    return Practice.getService(PlayerService.class).get(player);
  }

  public PracticePlayer get(PlayerEvent event) {
    return get(event.getPlayer());
  }
}
