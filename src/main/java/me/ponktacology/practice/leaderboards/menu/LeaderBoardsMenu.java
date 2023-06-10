package me.ponktacology.practice.leaderboards.menu;

import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.ladder.LadderService;
import me.ponktacology.practice.leaderboards.LeaderBoardsService;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.ItemBuilder;
import me.ponktacology.practice.util.menu.Button;
import me.ponktacology.practice.util.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeaderBoardsMenu extends Menu {

  @Override
  public String getTitle(Player player) {
    return "Leader boards";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    for (Ladder ladder : Practice.getService(LadderService.class).getAllLadders()) {
      buttons.put(buttons.size(), new LeaderInfoButton(ladder));
    }
    return buttons;
  }

  @RequiredArgsConstructor
  private static class LeaderInfoButton extends Button {
    private final Ladder ladder;

    @Override
    public ItemStack getButtonItem(Player player) {
      List<String> lore = new ArrayList<>();
      List<PracticePlayer> leaderBoardPlayers =
          Practice.getService(LeaderBoardsService.class).getLeaderBoardPlayers(ladder);
      for (int i = 0; i < leaderBoardPlayers.size(); i++) {
        PracticePlayer playerSession = leaderBoardPlayers.get(i);
        lore.add(
            ChatColor.GRAY
                + ""
                + (i + 1)
                + "."
                + playerSession.getName()
                + " "
                + playerSession.getElo(ladder)
                + "elo");
      }
      return new ItemBuilder(ladder.getIcon())
          .name(ladder.getDisplayName() + " - top10")
          .lore(lore)
          .build();
    }
  }
}
