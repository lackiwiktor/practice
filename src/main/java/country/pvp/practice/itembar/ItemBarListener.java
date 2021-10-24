package country.pvp.practice.itembar;

import com.google.inject.Inject;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ItemBarListener implements Listener {

  private final PlayerManager playerManager;

  @EventHandler(priority = EventPriority.MONITOR)
  public void clickEvent(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    PracticePlayer practicePlayer = playerManager.getAll(player);
    ItemStack item = event.getItem();

    if (item == null) return;

    Action action = event.getAction();

    if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

    if (!practicePlayer.isFighting()) {
      event.setCancelled(ItemBar.click(practicePlayer, item, BarInteract.LEFT_CLICK));
    }
  }
}
