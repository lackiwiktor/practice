package country.pvp.practice.player;

import com.google.inject.Inject;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.player.data.PlayerState;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.Arrays;

/**
 * This class sets up and handles a player who is in the lobby
 */
@Slf4j
public class PlayerProtectionListener extends PlayerListener {

    private final LobbyService lobbyService;

    @Inject
    public PlayerProtectionListener(PlayerManager playerManager, LobbyService lobbyService) {
        super(playerManager);
        this.lobbyService = lobbyService;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void playerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        PracticePlayer practicePlayer = get(event);
        lobbyService.moveToLobby(practicePlayer);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void entitySpawn(EntitySpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void breakEvent(BlockBreakEvent event) {
        cancelIfInState(event.getPlayer(), event, true, PlayerState.IN_LOBBY, PlayerState.EDITING_KIT, PlayerState.QUEUING, PlayerState.SPECTATING);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void placeBlock(BlockPlaceEvent event) {
        cancelIfInState(event.getPlayer(), event, true, PlayerState.IN_LOBBY, PlayerState.EDITING_KIT, PlayerState.QUEUING, PlayerState.SPECTATING);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void entityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        cancelIfInState((Player) event.getEntity(), event, false, PlayerState.IN_LOBBY, PlayerState.EDITING_KIT, PlayerState.QUEUING, PlayerState.SPECTATING);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void dropItem(PlayerDropItemEvent event) {
        cancelIfInState(event.getPlayer(), event, false, PlayerState.IN_LOBBY, PlayerState.EDITING_KIT, PlayerState.QUEUING, PlayerState.SPECTATING);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void pickupItem(PlayerPickupItemEvent event) {
        cancelIfInState(event.getPlayer(), event, false, PlayerState.IN_LOBBY, PlayerState.EDITING_KIT, PlayerState.QUEUING, PlayerState.SPECTATING);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void inventoryInteract(InventoryInteractEvent event) {
        cancelIfInState((Player) event.getWhoClicked(), event, true, PlayerState.IN_LOBBY, PlayerState.EDITING_KIT, PlayerState.QUEUING, PlayerState.SPECTATING);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void inventoryClick(InventoryClickEvent event) {
        cancelIfInState((Player) event.getWhoClicked(), event, true, PlayerState.IN_LOBBY, PlayerState.QUEUING, PlayerState.SPECTATING);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void foodLevelChange(FoodLevelChangeEvent event) {
        cancelIfInState((Player) event.getEntity(), event, false, PlayerState.IN_LOBBY, PlayerState.EDITING_KIT, PlayerState.QUEUING, PlayerState.SPECTATING);

        if (event.isCancelled()) {
            event.setFoodLevel(20);
        }
    }

    private void cancelIfInState(Player player, Cancellable event, boolean permBypass, PlayerState... states) {
        PracticePlayer practicePlayer = get(player);

        if (Arrays.asList(states).contains(practicePlayer.getState()) && (!permBypass || !practicePlayer.hasPermission("practice.admin"))) {
             event.setCancelled(true);
        }
    }
}
