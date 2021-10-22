package country.pvp.practice.itembar;

import country.pvp.practice.player.PracticePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ItemBarListener implements Listener {

  @EventHandler(priority = EventPriority.MONITOR)
  public void clickEvent(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    PracticePlayer practicePlayer = PracticePlayer.get(player);
    ItemStack item = event.getItem();

    if (item == null) return;

    Action action = event.getAction();

    if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

    if (!practicePlayer.isFighting()) {
      event.setCancelled(ItemBar.click(practicePlayer, item, BarInteract.LEFT_CLICK));
    }
  }
}
