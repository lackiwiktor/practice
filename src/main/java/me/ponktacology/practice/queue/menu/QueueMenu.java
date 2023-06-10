package me.ponktacology.practice.queue.menu;

import com.google.common.collect.Maps;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.PracticePreconditions;
import me.ponktacology.practice.match.MatchService;
import me.ponktacology.practice.util.ItemBuilder;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.queue.Queue;
import me.ponktacology.practice.queue.QueueService;
import me.ponktacology.practice.util.menu.Button;
import me.ponktacology.practice.util.menu.Menu;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@RequiredArgsConstructor
public class QueueMenu extends Menu {

  private final boolean ranked;
  private final PracticePlayer practicePlayer;

  @Override
  public String getTitle(Player player) {
    return "Select kit...";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    QueueService queueService = Practice.getService(QueueService.class);
    for (Queue queue : queueService.getQueues(ranked)) {
      buttons.put(buttons.size(), new QueueButton(queue));
    }

    return buttons;
  }

  @RequiredArgsConstructor
  private class QueueButton extends Button {

    private final Queue queue;

    @Override
    public ItemStack getButtonItem(Player player) {
      Ladder ladder = queue.getLadder();
      return new ItemBuilder(ladder.getIcon())
          .name(ladder.getDisplayName())
          .hideAll()
          .lore(
              ChatColor.GRAY
                  + "In Fights: "
                  + ChatColor.WHITE
                  + Practice.getService(MatchService.class).getPlayersInFightCount(ladder, ranked),
              ChatColor.GRAY + "In Queue: " + ChatColor.WHITE + queue.size())
          .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
      if (!PracticePreconditions.canJoinQueue(practicePlayer)) return;

      queue.addPlayer(practicePlayer);
      player.getOpenInventory().close();
    }
  }
}
