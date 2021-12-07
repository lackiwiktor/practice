package country.pvp.practice.listeners;

import com.google.inject.Inject;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.player.PlayerListener;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.player.data.PlayerState;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;

/**
 * This class sets up and handles a player who is in the lobby
 */
public class PlayerLobbyListener extends PlayerListener {

    private final LobbyService lobbyService;

    @Inject
    public PlayerLobbyListener(PlayerManager playerManager, LobbyService lobbyService) {
        super(playerManager);
        this.lobbyService = lobbyService;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void playerJoin(PlayerJoinEvent event) {
        PlayerSession playerSession = get(event);
        lobbyService.moveToLobby(playerSession);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void entitySpawn(EntitySpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void breakEvent(BlockBreakEvent event) {
        cancelIfInState(event.getPlayer(), event, true, PlayerState.IN_LOBBY, PlayerState.EDITING_KIT, PlayerState.QUEUING, PlayerState.SPECTATING);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void playerMove(PlayerMoveEvent event) {
        if (event.getTo().getY() <= 40) {
            cancelIfInState(event.getPlayer(), event, false, PlayerState.IN_LOBBY, PlayerState.QUEUING);

            if (event.isCancelled()) {
                event.getPlayer().teleport(lobbyService.getSpawnLocation());
                event.getPlayer().setVelocity(new Vector());
                event.setCancelled(false);
            }
        }
    }

    private void cancelIfInState(Player player, Cancellable event, boolean permBypass, PlayerState... states) {
        PlayerSession playerSession = get(player);

        if (Arrays.asList(states).contains(playerSession.getState()) && (!permBypass || !playerSession.hasPermission("practice.admin"))) {
            event.setCancelled(true);
        }
    }
}
