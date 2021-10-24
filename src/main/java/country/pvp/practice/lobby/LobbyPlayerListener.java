package country.pvp.practice.lobby;

import com.google.inject.Inject;
import country.pvp.practice.itembar.ItemBar;
import country.pvp.practice.player.PlayerListener;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.visibility.VisibilityUpdater;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * This class sets up and handles a player who is in the lobby
 */
public class LobbyPlayerListener extends PlayerListener {

    private final VisibilityUpdater visibilityUpdater;

    @Inject
    public LobbyPlayerListener(PlayerManager playerManager, VisibilityUpdater visibilityUpdater) {
        super(playerManager);
        this.visibilityUpdater = visibilityUpdater;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void joinEvent(PlayerJoinEvent event) {
        PracticePlayer practicePlayer = get(event);

        ItemBar.LOBBY.apply(practicePlayer);
        visibilityUpdater.update(practicePlayer);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void breakEvent(BlockBreakEvent event) {
        cancelIfInLobby(event.getPlayer(), event, true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void placeEvent(BlockPlaceEvent event) {
        cancelIfInLobby(event.getPlayer(), event, true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void damageEvent(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        cancelIfInLobby((Player) event.getEntity(), event, false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void dropEvent(PlayerDropItemEvent event) {
        cancelIfInLobby(event.getPlayer(), event, false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void dropEvent(InventoryClickEvent event) {
        cancelIfInLobby((Player) event.getWhoClicked(), event, true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void hungerEvent(FoodLevelChangeEvent event) {
        cancelIfInLobby((Player) event.getEntity(), event, false);

        if (event.isCancelled()) {
            event.setFoodLevel(20);
        }
    }

    private void cancelIfInLobby(Player player, Cancellable event, boolean permBypass) {
        PracticePlayer practicePlayer = get(player);

        if (practicePlayer.isInLobby() && (!permBypass || !practicePlayer.hasPermission("practice.admin"))) {
            event.setCancelled(true);
        }
    }
}
