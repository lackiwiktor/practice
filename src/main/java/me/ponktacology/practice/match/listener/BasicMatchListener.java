package me.ponktacology.practice.match.listener;

import me.ponktacology.practice.Messages;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.MatchState;
import me.ponktacology.practice.match.info.PlayerInfoTracker;
import me.ponktacology.practice.match.pearl_cooldown.PearlCooldownTracker;
import me.ponktacology.practice.match.statistics.PlayerStatisticsTracker;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.PracticePlayerListener;
import me.ponktacology.practice.util.TaskDispatcher;
import me.ponktacology.practice.util.TimeUtil;
import me.ponktacology.practice.util.message.Messenger;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class BasicMatchListener extends PracticePlayerListener {

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void itemSpawnEvent(ItemSpawnEvent event) {
    Entity entity = event.getEntity();

    // TODO: Run async??
    TaskDispatcher.runLater(entity::remove, 10L, TimeUnit.SECONDS);
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void entityDamageEvent(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) return;
    PracticePlayer damagedPlayer = get((Player) event.getEntity());

    if (!damagedPlayer.isInMatch()) return;

    Match match = damagedPlayer.getCurrentMatch();

    if (match.getState() != MatchState.IN_PROGRESS) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player)) return;

    PracticePlayer damagedPlayer = get((Player) event.getEntity());
    if (!damagedPlayer.isInMatch()) return;

    Match match = damagedPlayer.getCurrentMatch();

    if (match.getState() != MatchState.IN_PROGRESS) {
      event.setCancelled(true);
      return;
    }

    PlayerInfoTracker playerInfoTracker = match.getInfoTracker();

    if (!playerInfoTracker.isAlive(damagedPlayer)) {
      event.setCancelled(true);
      return;
    }

    if (!(event.getDamager() instanceof Player)) return;

    PracticePlayer damagerPlayer = get((Player) event.getDamager());

    if (!match.isInMatch(damagerPlayer)
        || !playerInfoTracker.isAlive(damagerPlayer)
        || match.isOnSameTeam(damagedPlayer, damagerPlayer)) {
      event.setCancelled(true);
      return;
    }

    playerInfoTracker.setLastAttacker(damagedPlayer, damagerPlayer);

    PlayerStatisticsTracker statisticsTracker = match.getStatisticsTracker();

    statisticsTracker.onPlayerAttack(damagerPlayer);
    statisticsTracker.onPlayerBeingAttacked(damagedPlayer);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void playerDeathEvent(PlayerDeathEvent event) {
    event.setDeathMessage(null);
    PracticePlayer player = get(event.getEntity());

    if (!player.isInMatch()) return;

    event.setDroppedExp(0);
    Match match = player.getCurrentMatch();
    match.onPlayerDeath(player, event.getDrops());
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void breakBlockEvent(BlockBreakEvent event) {
    PracticePlayer practicePlayer = get(event.getPlayer());

    if (practicePlayer.isInMatch()) {
      Match match = practicePlayer.getCurrentMatch();

      event.setCancelled(match.getState() != MatchState.IN_PROGRESS || !match.isBuild());
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void placeBlockEvent(BlockPlaceEvent event) {
    PracticePlayer practicePlayer = get(event.getPlayer());

    if (!practicePlayer.isInMatch()) {
      return;
    }

    Match match = practicePlayer.getCurrentMatch();
    event.setCancelled(match.getState() != MatchState.IN_PROGRESS || !match.isBuild());
  }

  @EventHandler
  public void foodLevelChangeEvent(FoodLevelChangeEvent event) {
    PracticePlayer practicePlayer = get((Player) event.getEntity());

    if (!practicePlayer.isInMatch()) {
      return;
    }

    Match match = practicePlayer.getCurrentMatch();

    if (!match.isHungerDecay()) {
      event.setFoodLevel(20);
    }
  }

  @EventHandler
  public void playerQuitEvent(PlayerQuitEvent event) {
    PracticePlayer player = get(event);
    if (!player.isInMatch()) return;

    Match match = player.getCurrentMatch();
    match.onPlayerDisconnect(player);
  }

  @EventHandler
  public void potionSplashEvent(PotionSplashEvent event) {
    if (event.getPotion().getShooter() instanceof Player) {
      Player shooter = (Player) event.getPotion().getShooter();
      PracticePlayer player = get(shooter);

      if (!player.isInMatch()) return;
      Match match = player.getCurrentMatch();
      PlayerStatisticsTracker statisticsTracker = match.getStatisticsTracker();
      statisticsTracker.increaseThrownPots(player);

      if (event.getIntensity(shooter) <= 0.5D) {
        statisticsTracker.increaseMissedPots(player);
      }

      for (LivingEntity entity : event.getAffectedEntities()) {
        if (entity instanceof Player) {
          PracticePlayer affectedPlayer = get((Player) entity);

          if (!affectedPlayer.isInMatch() || !match.isInMatch(affectedPlayer))
            event.setIntensity(entity, 0);
        }
      }
    }
  }

  @EventHandler
  public void playerConsumeEvent(PlayerItemConsumeEvent event) {
    Player player = event.getPlayer();
    PracticePlayer practicePlayer = get(player);

    if (event.getItem().getType() == Material.POTION && practicePlayer.isInMatch()) {
      TaskDispatcher.runLater(
          () -> player.setItemInHand(new ItemStack(Material.AIR)), 1L, TimeUnit.MILLISECONDS);
    }
  }

  @EventHandler
  public void pickupItemEvent(PlayerPickupItemEvent event) {
    PracticePlayer player = get(event);

    if (player.isInMatch()) {
      Match currentMatch = player.getCurrentMatch();
      PlayerInfoTracker infoTracker = currentMatch.getInfoTracker();

      if (currentMatch.getState() != MatchState.IN_PROGRESS || !infoTracker.isAlive(player)) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void itemDropEvent(PlayerDropItemEvent event) {
    PracticePlayer player = get(event);

    if (player.isInMatch()) {
      Match currentMatch = player.getCurrentMatch();

      if (currentMatch.getState() != MatchState.IN_PROGRESS) event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void playerInteractEvent(PlayerInteractEvent event) {
    ItemStack item = event.getItem();
    if (item == null || item.getType() != Material.ENDER_PEARL) return;

    Action action = event.getAction();
    if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

    PracticePlayer player = get(event);

    if (!player.isInMatch()) return;

    Player bukkitPlayer = event.getPlayer();
    Match match = player.getCurrentMatch();

    if (match.getState() != MatchState.IN_PROGRESS) {
      Messenger.message(bukkitPlayer, "You can't use pearls right now.");
      event.setCancelled(true);
      return;
    }

    PearlCooldownTracker cooldownTracker = match.getCooldownTracker();

    if (!cooldownTracker.hasExpired(player)) {
      String time = TimeUtil.millisToSeconds(cooldownTracker.getRemaining(player));
      Messenger.message(
          bukkitPlayer,
          Messages.MATCH_PLAYER_PEARL_COOLDOWN.match(
              "{time}", time + ("1.0".equals(time) ? "" : "s")));
      TaskDispatcher.runLater(bukkitPlayer::updateInventory, 100L, TimeUnit.MILLISECONDS);
      event.setCancelled(true);
    } else {
      cooldownTracker.reset(player);
    }
  }
}
