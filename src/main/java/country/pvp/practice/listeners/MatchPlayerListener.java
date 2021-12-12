package country.pvp.practice.listeners;

import com.google.inject.Inject;
import country.pvp.practice.concurrent.TaskDispatcher;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.MatchState;
import country.pvp.practice.message.Messager;
import country.pvp.practice.message.Messages;
import country.pvp.practice.player.PlayerListener;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.time.TimeUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class MatchPlayerListener extends PlayerListener {

    @Inject
    public MatchPlayerListener(PlayerManager playerManager) {
        super(playerManager);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void entityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        PlayerSession damagedPlayer = get((Player) event.getEntity());

        if (!damagedPlayer.isInMatch()) return;

        Match match = damagedPlayer.getCurrentMatch();

        if (match.getState() != MatchState.FIGHT) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        PlayerSession damagedPlayer = get((Player) event.getEntity());
        if (!damagedPlayer.isInMatch()) return;

        Match match = damagedPlayer.getCurrentMatch();

        if (match.getState() != MatchState.FIGHT) {
            event.setCancelled(true);
            return;
        }

        if (!match.isAlive(damagedPlayer)) {
            event.setCancelled(true);
            return;
        }

        if (!(event.getDamager() instanceof Player)) return;

        PlayerSession damagerPlayer = get((Player) event.getDamager());

        if (!match.isInMatch(damagerPlayer) || !match.isAlive(damagerPlayer)) {
            event.setCancelled(true);
            return;
        }

        if (match.isOnSameTeam(damagedPlayer, damagerPlayer)) {
            event.setCancelled(true);
            return;
        }

        damagerPlayer.handleHit();
        damagedPlayer.handleBeingHit(damagerPlayer);
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        PlayerSession player = get(event.getEntity());
        if (!player.isInMatch()) return;
        Match match = player.getCurrentMatch();
        match.handleDeath(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void breakBlock(BlockBreakEvent event) {
        PlayerSession playerSession = get(event.getPlayer());

        if (playerSession.isInMatch()) {
            Match match = playerSession.getCurrentMatch();

            event.setCancelled(match.getState() != MatchState.FIGHT || !match.isBuild());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void placeBlock(BlockPlaceEvent event) {
        PlayerSession playerSession = get(event.getPlayer());

        if (playerSession.isInMatch()) {
            Match match = playerSession.getCurrentMatch();

            event.setCancelled(match.getState() != MatchState.FIGHT || !match.isBuild());
        }
    }


    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        PlayerSession player = get(event);
        if (!player.isInMatch()) return;

        Match match = player.getCurrentMatch();
        match.handleDisconnect(player);
        event.getPlayer().setHealth(0);
    }

    @EventHandler
    public void potionSplash(PotionSplashEvent event) {
        if (event.getPotion().getShooter() instanceof Player) {
            Player shooter = (Player) event.getPotion().getShooter();
            PlayerSession player = get(shooter);

            if (!player.isInMatch()) return;

            player.increaseThrownPots();

            if (event.getIntensity(shooter) <= 0.5D) {
                player.increaseMissedPots();
            }

            Match match = player.getCurrentMatch();
            for (LivingEntity entity : event.getAffectedEntities()) {
                if (entity instanceof Player) {
                    PlayerSession affectedPlayer = get((Player) entity);

                    if (!affectedPlayer.isInMatch() || !match.isInMatch(affectedPlayer))
                        event.setIntensity(entity, 0);
                }
            }
        }
    }

    @EventHandler
    public void playerConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        PlayerSession playerSession = get(player);

        if (event.getItem().getType() == Material.POTION && playerSession.isInMatch()) {
            TaskDispatcher.runLater(() -> player.setItemInHand(new ItemStack(Material.AIR)), 1L, TimeUnit.MILLISECONDS);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.ENDER_PEARL) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        PlayerSession playerSession = get(event);

        if (!playerSession.isInMatch()) return;

        Player player = event.getPlayer();
        Match match = playerSession.getCurrentMatch();

            if (match.getState() != MatchState.FIGHT) {
                Messager.message(playerSession, "You can't use pearls right now.");
                event.setCancelled(true);
                return;
            }

            if (!playerSession.hasPearlCooldownExpired()) {
                String time = TimeUtil.millisToSeconds(playerSession.getRemainingPearlCooldown());
                Messager.message(playerSession, Messages.MATCH_PLAYER_PEARL_COOLDOWN.match("{time}",
                        time + ("1.0".equals(time) ? "" : "s")));
                TaskDispatcher.runLater(player::updateInventory, 100L, TimeUnit.MILLISECONDS);
                event.setCancelled(true);
            } else {
                playerSession.resetPearlCooldown();
            }
    }
}
