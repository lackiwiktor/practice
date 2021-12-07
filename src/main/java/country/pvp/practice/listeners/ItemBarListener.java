package country.pvp.practice.listeners;

import com.google.inject.Inject;
import country.pvp.practice.itembar.ItemBarService;
import country.pvp.practice.player.PlayerListener;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


public class ItemBarListener extends PlayerListener {

    private final ItemBarService itemBarService;

    @Inject
    public ItemBarListener(PlayerManager playerManager, ItemBarService itemBarService) {
        super(playerManager);
        this.itemBarService = itemBarService;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void clickEvent(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        PlayerSession playerSession = get(event);
        if (!(playerSession.isInMatch() || playerSession.isInEditor()))
            event.setCancelled(itemBarService.handleInteract(playerSession, item));
    }
}
