package me.ponktacology.practice.match.listener;

import lombok.RequiredArgsConstructor;
import me.ponktacology.practice.Messages;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.MatchService;
import me.ponktacology.practice.match.MatchState;
import me.ponktacology.practice.match.event.MatchPlayerDamageByPlayerEvent;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.PracticePlayerListener;
import me.ponktacology.practice.util.EventUtil;
import me.ponktacology.practice.util.TaskDispatcher;
import me.ponktacology.practice.util.TimeUtil;
import me.ponktacology.practice.util.message.Messenger;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class MatchGeneralListener extends PracticePlayerListener {

  private final MatchService matchService;

  @EventHandler
  public void onWorldLoad(WorldLoadEvent event) {
    event.getWorld().getEntities().clear();
    event.getWorld().setDifficulty(Difficulty.HARD);
  }

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
    if (!matchService.isInMatch(damagedPlayer)) return;

    Match match = matchService.getPlayerMatch(damagedPlayer);

    if (match.getState() != MatchState.IN_PROGRESS) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player)) return;

    PracticePlayer damagedPlayer = get((Player) event.getEntity());
    if (!matchService.isInMatch(damagedPlayer)) return;

    Match match = matchService.getPlayerMatch(damagedPlayer);

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

    if (!match.isParticipating(damagerPlayer)
        || !playerInfoTracker.isAlive(damagerPlayer)
        || match.isOnSameTeam(damagedPlayer, damagerPlayer)) {
      event.setCancelled(true);
      return;
    }

    EventUtil.callEvent(
        new MatchPlayerDamageByPlayerEvent(match, damagedPlayer, damagerPlayer, event));

    playerInfoTracker.setLastAttacker(damagedPlayer, damagerPlayer);

    PlayerStatisticsTracker statisticsTracker = match.getStatisticsTracker();

    statisticsTracker.onPlayerAttack(damagerPlayer);
    statisticsTracker.onPlayerBeingAttacked(damagedPlayer);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void playerDeathEvent(PlayerDeathEvent event) {
    event.setDeathMessage(null);
    PracticePlayer player = get(event.getEntity());

    if (!matchService.isInMatch(player)) return;

    event.setDroppedExp(0);
    Match match = matchService.getPlayerMatch(player);
    match.markAsDead(player, event.getDrops());
  }

  @EventHandler
  public void foodLevelChangeEvent(FoodLevelChangeEvent event) {
    PracticePlayer player = get((Player) event.getEntity());
    if (!matchService.isInMatch(player)) return;
    Match match = matchService.getPlayerMatch(player);

    if (!match.getLadder().isHungerDecay()) {
      event.setFoodLevel(20);
    }
  }

  @EventHandler
  public void playerQuitEvent(PlayerQuitEvent event) {
    PracticePlayer player = get(event);
    if (!matchService.isInMatch(player)) return;
    Match match = matchService.getPlayerMatch(player);
    match.markAsDisconnected(player);
  }

  @EventHandler
  public void potionSplashEvent(PotionSplashEvent event) {
    if (event.getPotion().getShooter() instanceof Player) {
      Player shooter = (Player) event.getPotion().getShooter();
      PracticePlayer player = get(shooter);
      if (!matchService.isInMatch(player)) return;
      Match match = matchService.getPlayerMatch(player);
      PlayerStatisticsTracker statisticsTracker = match.getStatisticsTracker();
      statisticsTracker.increaseThrownPots(player);

      if (event.getIntensity(shooter) <= 0.5D) {
        statisticsTracker.increaseMissedPots(player);
      }

      for (LivingEntity entity : event.getAffectedEntities()) {
        if (entity instanceof Player) {
          PracticePlayer affectedPlayer = get((Player) entity);

          if (!matchService.isInMatch(affectedPlayer) || !match.isParticipating(affectedPlayer))
            event.setIntensity(entity, 0);
        }
      }
    }
  }

  @EventHandler
  public void playerConsumeEvent(PlayerItemConsumeEvent event) {
    Player player = event.getPlayer();
    PracticePlayer practicePlayer = get(player);

    if (matchService.isInMatch(practicePlayer) && event.getItem().getType() == Material.POTION) {
      TaskDispatcher.runLater(
          () -> player.setItemInHand(new ItemStack(Material.AIR)), 1L, TimeUnit.MILLISECONDS);
    }
  }

  @EventHandler
  public void pickupItemEvent(PlayerPickupItemEvent event) {
    PracticePlayer player = get(event);

    if (matchService.isInMatch(player)) {
      Match currentMatch = matchService.getPlayerMatch(player);
      PlayerInfoTracker infoTracker = currentMatch.getInfoTracker();

      if (currentMatch.getState() != MatchState.IN_PROGRESS || !infoTracker.isAlive(player)) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void itemDropEvent(PlayerDropItemEvent event) {
    PracticePlayer player = get(event);

    if (matchService.isInMatch(player)) {
      Match currentMatch = matchService.getPlayerMatch(player);

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

    if (!matchService.isInMatch(player)) return;

    Player bukkitPlayer = event.getPlayer();
    Match match = matchService.getPlayerMatch(player);

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
