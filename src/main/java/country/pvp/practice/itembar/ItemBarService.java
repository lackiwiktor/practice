package country.pvp.practice.itembar;

import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.util.PlayerUtil;
import org.bukkit.inventory.ItemStack;

public class ItemBarService {

  public boolean handleInteract(PlayerSession player, ItemStack item) {
      ItemBar itemBar = ItemBar.get(player);
      if (itemBar == null) return false;

      for (ItemBarItem itemBarItem : itemBar.getItems()) {
          if (itemBarItem == null) continue;

          if (itemBarItem.isSimilar(item)) {
              itemBarItem.click(player);
              return true;
          }
        }

        return false;
    }

    public void apply(PlayerSession player) {
        PlayerUtil.clearInventory(player.getPlayer());
        ItemBar itemBar = ItemBar.get(player);

        if (itemBar == null) return;
        itemBar.apply(player);
    }
}
