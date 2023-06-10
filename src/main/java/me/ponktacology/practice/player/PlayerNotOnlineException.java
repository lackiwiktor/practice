package me.ponktacology.practice.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerNotOnlineException extends IllegalStateException {

  public PlayerNotOnlineException(Player player) {
    super("Name: " + player.getName() + ", Is online: " + player.isOnline());

    if (player.isOnline()) {
      if (Bukkit.isPrimaryThread()) player.kickPlayer("Error");
    }
  }
}
