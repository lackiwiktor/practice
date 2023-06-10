package me.ponktacology.practice.arena;

import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.arena.match.MatchArena;
import me.ponktacology.practice.util.data.Callback;
import me.ponktacology.practice.util.menu.Button;
import me.ponktacology.practice.util.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@RequiredArgsConstructor
public class ArenaChooseMenu extends Menu {

  private final boolean close;
  private final Callback<MatchArena> callback;

  @Override
  public String getTitle(Player player) {
    return "Choose arena";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    for (MatchArena arena : Practice.getService(ArenaService.class).getMatchArenas()) {
      buttons.put(buttons.size(), new LadderChoiceButton(arena));
    }

    return buttons;
  }

  @RequiredArgsConstructor
  private class LadderChoiceButton extends Button {

    private final MatchArena arena;

    @Override
    public ItemStack getButtonItem(Player player) {
      return arena.getIcon();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
      if (clickType.isLeftClick()) {
        if (close) player.closeInventory();
        callback.call(arena);
      }
    }
  }
}
