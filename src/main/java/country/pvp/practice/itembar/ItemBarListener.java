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
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ItemBarListener implements Listener {

    private final PlayerManager playerManager;
    private final ItemBarManager itemBarManager;

    @EventHandler(priority = EventPriority.MONITOR)
    public void clickEvent( PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        PracticePlayer practicePlayer = playerManager.get(player);

        if (!(practicePlayer.isInMatch() || practicePlayer.isInEditor()))
            event.setCancelled(itemBarManager.click(practicePlayer, item, BarInteract.RIGHT_CLICK));
    }
}
