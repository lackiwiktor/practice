package me.ponktacology.practice.kit;

import com.google.common.collect.Maps;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.ladder.LadderService;
import me.ponktacology.practice.util.data.Callback;
import me.ponktacology.practice.util.menu.Button;
import me.ponktacology.practice.util.menu.Menu;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@RequiredArgsConstructor
public class KitChooseMenu extends Menu {

  private final Callback<Ladder> callback;

  @Override
  public String getTitle(Player player) {
    return "Choose kit";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    for (Ladder ladder : Practice.getService(LadderService.class).getAllLadders()) {
      buttons.put(buttons.size(), new KitChoiceButton(ladder));
    }

    return buttons;
  }

  @RequiredArgsConstructor
  private class KitChoiceButton extends Button {

    private final Ladder ladder;

    @Override
    public ItemStack getButtonItem(Player player) {
      return ladder.getIcon();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
      if (clickType.isLeftClick()) {
        player.closeInventory();
        callback.call(ladder);
      }
    }
  }
}
