package me.ponktacology.practice.lobby.listener;

import me.ponktacology.practice.lobby.LobbyService;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.PracticePlayerListener;
import me.ponktacology.practice.player.data.PlayerState;
import me.ponktacology.practice.util.LocationUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.util.Vector;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.Arrays;

/** This class sets up and handles a player who is in the lobby */
@RequiredArgsConstructor
public class LobbyListener extends PracticePlayerListener {

  private final LobbyService lobbyService;

  @EventHandler
  public void spawnEvent(PlayerSpawnLocationEvent event) {
    event.setSpawnLocation(lobbyService.getSpawnLocation());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void joinEvent(PlayerJoinEvent event) {
    PracticePlayer practicePlayer = get(event);
    lobbyService.prepareForLobby(practicePlayer);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void breakBlockEvent(BlockBreakEvent event) {
    cancelIfInState(
        event.getPlayer(),
        event,
        true,
        PlayerState.IN_LOBBY,
        PlayerState.EDITING_KIT,
        PlayerState.QUEUING,
        PlayerState.SPECTATING);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void placeBlockEvent(BlockPlaceEvent event) {
    cancelIfInState(
        event.getPlayer(),
        event,
        true,
        PlayerState.IN_LOBBY,
        PlayerState.EDITING_KIT,
        PlayerState.QUEUING,
        PlayerState.SPECTATING);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void entityDamageEvent(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) return;
    cancelIfInState(
        (Player) event.getEntity(),
        event,
        false,
        PlayerState.IN_LOBBY,
        PlayerState.EDITING_KIT,
        PlayerState.QUEUING,
        PlayerState.SPECTATING);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void dropItemEvent(PlayerDropItemEvent event) {
    cancelIfInState(
        event.getPlayer(),
        event,
        false,
        PlayerState.IN_LOBBY,
        PlayerState.EDITING_KIT,
        PlayerState.QUEUING,
        PlayerState.SPECTATING);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void pickupItemEvent(PlayerPickupItemEvent event) {
    cancelIfInState(
        event.getPlayer(),
        event,
        false,
        PlayerState.IN_LOBBY,
        PlayerState.EDITING_KIT,
        PlayerState.QUEUING,
        PlayerState.SPECTATING);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void inventoryInteractEvent(InventoryInteractEvent event) {
    cancelIfInState(
        (Player) event.getWhoClicked(),
        event,
        true,
        PlayerState.IN_LOBBY,
        PlayerState.QUEUING,
        PlayerState.SPECTATING);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void inventoryClickEvent(InventoryClickEvent event) {
    cancelIfInState(
        (Player) event.getWhoClicked(),
        event,
        true,
        PlayerState.IN_LOBBY,
        PlayerState.QUEUING,
        PlayerState.SPECTATING);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void foodLevelChangeEvent(FoodLevelChangeEvent event) {
    cancelIfInState(
        (Player) event.getEntity(),
        event,
        false,
        PlayerState.IN_LOBBY,
        PlayerState.EDITING_KIT,
        PlayerState.QUEUING,
        PlayerState.SPECTATING);

    if (event.isCancelled()) {
      event.setFoodLevel(20);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void playerMoveEvent(PlayerMoveEvent event) {
    if (!LocationUtil.hasChanged(event.getFrom(), event.getTo())) return;

    if (lobbyService.shouldRebound(event.getTo())) {
      cancelIfInState(event.getPlayer(), event, true, PlayerState.IN_LOBBY, PlayerState.QUEUING);

      if (event.isCancelled()) {
        event.getPlayer().teleport(lobbyService.getSpawnLocation());
        event.getPlayer().setVelocity(new Vector());
        event.setCancelled(false);
      }
    }
  }

  private void cancelIfInState(
      Player player, Cancellable event, boolean permBypass, PlayerState... states) {
    PracticePlayer practicePlayer = get(player);

    if (Arrays.asList(states).contains(practicePlayer.getState())
        && (!permBypass
            || !(practicePlayer.hasPermission("practice.admin")
                && GameMode.CREATIVE.equals(player.getGameMode())))) {
      event.setCancelled(true);
    }
  }
}
