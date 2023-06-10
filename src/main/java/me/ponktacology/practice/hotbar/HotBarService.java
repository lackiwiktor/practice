package me.ponktacology.practice.hotbar;

import com.google.common.base.Preconditions;
import me.ponktacology.practice.Service;
import me.ponktacology.practice.hotbar.listener.HotBarListener;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HotBarService extends Service {

  @Override
  public void configure() {
    addListener(new HotBarListener(this));
  }

  public boolean handleInteract(PracticePlayer player, ItemStack item) {
    HotBarItem matchingItem = HotBar.getMatchingItemBarItem(item);
    if (matchingItem == null) return false;
    matchingItem.click(player);
    return true;
  }

  public void apply(Player player) {
    PlayerUtil.clearInventory(player);
    HotBar hotBar = HotBar.get(player);
    if (hotBar == null) return;
    player.setBar(hotBar.items);
  }
}
