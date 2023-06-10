package me.ponktacology.practice.queue.menu;

import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.settings.Setting;
import me.ponktacology.practice.settings.Settings;
import me.ponktacology.practice.util.menu.Button;
import me.ponktacology.practice.util.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@RequiredArgsConstructor
public class SettingsMenu extends Menu {

  private final Settings settings;
  private final PracticePlayer practicePlayer;

  @Override
  public String getTitle(Player player) {
    return "Settings";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    for (Setting<?> setting : settings.getSettings()) {
      buttons.put(buttons.size(), new SettingButton(setting));
    }

    return buttons;
  }

  @RequiredArgsConstructor
  private class SettingButton extends Button {

    private final Setting<?> setting;

    @Override
    public ItemStack getButtonItem(Player player) {
      return setting.getIcon();
    }

    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
      if (clickType.isLeftClick()) {
        setting.toggle(practicePlayer);
        return true;
      }

      return false;
    }
  }
}
