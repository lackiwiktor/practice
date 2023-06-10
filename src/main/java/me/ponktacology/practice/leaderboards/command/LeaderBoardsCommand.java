package me.ponktacology.practice.leaderboards.command;

import me.ponktacology.practice.leaderboards.menu.LeaderBoardsMenu;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import org.bukkit.entity.Player;

public class LeaderBoardsCommand {

  @Command(value = {"leaderboards", "lb", "top"})
  public void leaderBoard(@Sender Player sender) {
    new LeaderBoardsMenu().openMenu(sender);
  }
}
