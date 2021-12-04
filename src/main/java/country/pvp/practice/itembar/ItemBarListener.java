package country.pvp.practice.itembar;

import com.google.inject.Inject;
import country.pvp.practice.player.PlayerListener;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


public class ItemBarListener extends PlayerListener {

    private final ItemBarManager itemBarManager;

    @Inject
    public ItemBarListener(PlayerManager playerManager, ItemBarManager itemBarManager) {
        super(playerManager);
        this.itemBarManager = itemBarManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void clickEvent(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        PlayerSession playerSession = get(event);
        if (!(playerSession.isInMatch() || playerSession.isInEditor()))
            event.setCancelled(itemBarManager.click(playerSession, item, BarInteract.RIGHT_CLICK));
    }
}
