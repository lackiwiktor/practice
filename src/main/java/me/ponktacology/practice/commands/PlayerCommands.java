package me.ponktacology.practice.commands;

import me.ponktacology.practice.Practice;
import me.ponktacology.practice.player.PlayerService;
import me.ponktacology.practice.player.PracticePlayer;
import org.bukkit.entity.Player;

public class PlayerCommands {
  public PracticePlayer get(Player player) {
    return Practice.getService(PlayerService.class).get(player);
  }
}
