package country.pvp.practice.match;

import com.google.inject.Inject;
import country.pvp.practice.concurrent.TaskDispatcher;
import country.pvp.practice.message.Messager;
import country.pvp.practice.message.Messages;
import country.pvp.practice.player.PlayerListener;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PracticePlayer;
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
        PracticePlayer damagedPlayer = get((Player) event.getEntity());

        if (!damagedPlayer.isInMatch()) return;

        Match match = damagedPlayer.getCurrentMatch();

        if (match.getState() != MatchState.FIGHT) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        PracticePlayer damagedPlayer = get((Player) event.getEntity());
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

        PracticePlayer damagerPlayer = get((Player) event.getDamager());

        if (!match.isInMatch(damagerPlayer) || !match.isAlive(damagerPlayer)) {
            event.setCancelled(true);
            return;
        }

        if (match.getTeam(damagedPlayer).hasPlayer(damagerPlayer)) {
            event.setCancelled(true);
            return;
        }

        damagerPlayer.handleHit();
        damagedPlayer.handleBeingHit(damagerPlayer);
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        PracticePlayer player = get(event.getEntity());
        if (!player.isInMatch()) return;
        Match match = player.getCurrentMatch();
        match.handleDeath(player);
    }

    /*
    @EventHandler
    public void playerRespawn(PlayerRespawnEvent event) {
        System.out.println("NOT HANDLED??");

        PracticePlayer player = get(event);
        if (!player.isInMatch()) return;

        event.setRespawnLocation(event.getPlayer().getLocation());

 Match match = practicePlayer.getCurrentMatch();
        match.handleRespawn(player);

        System.out.println("NOT HANDLED??2");
    }
     */

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void breakBlock(BlockBreakEvent event) {
        PracticePlayer practicePlayer = get(event.getPlayer());

        if (practicePlayer.isInMatch()) {
            Match match = practicePlayer.getCurrentMatch();

            event.setCancelled(match.getState() != MatchState.FIGHT || !match.isBuild());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void placeBlock(BlockPlaceEvent event) {
        PracticePlayer practicePlayer = get(event.getPlayer());

        if (practicePlayer.isInMatch()) {
            Match match = practicePlayer.getCurrentMatch();

            event.setCancelled(match.getState() != MatchState.FIGHT || !match.isBuild());
        }
    }


    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        PracticePlayer player = get(event);
        if (!player.isInMatch()) return;

        Match match = player.getCurrentMatch();
        match.handleDisconnect(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void potionSplash(PotionSplashEvent event) {
        if (event.getPotion().getShooter() instanceof Player) {
            Player shooter = (Player) event.getPotion().getShooter();
            PracticePlayer player = playerManager.get(shooter);

            if (!player.isInMatch()) return;

            player.increaseThrownPots();

            if (event.getIntensity(shooter) <= 0.5D) {
                player.increaseMissedPots();
            }

            Match match = player.getCurrentMatch();
            for (LivingEntity entity : event.getAffectedEntities()) {
                if (entity instanceof Player) {
                    PracticePlayer affectedPlayer = playerManager.get((Player) entity);

                    if (!affectedPlayer.isInMatch() || !match.isInMatch(affectedPlayer))
                        event.setIntensity(entity, 0);
                }
            }
        }
    }

    @EventHandler
    public void playerConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        PracticePlayer practicePlayer = get(player);

        if (practicePlayer.isInMatch()) {
            if (event.getItem().getType() == Material.POTION) {
                TaskDispatcher.runLater(() -> player.setItemInHand(new ItemStack(Material.AIR)), 1L, TimeUnit.MILLISECONDS);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void playerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        PracticePlayer practicePlayer = get(event);

        if (!practicePlayer.isInMatch()) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item.getType() == Material.ENDER_PEARL) {
            Match match = practicePlayer.getCurrentMatch();

            if (match.getState() != MatchState.FIGHT) {
                event.setCancelled(true);
                return;
            }

            if (!practicePlayer.hasPearlCooldownExpired()) {
                String time = TimeUtil.millisToSeconds(practicePlayer.getRemainingPearlCooldown());
                Messager.message(player, Messages.MATCH_PLAYER_PEARL_COOLDOWN.match("{time}",
                        time + (time.equalsIgnoreCase("1.0") ? "" : "s")));
                event.setCancelled(true);
            } else {
                practicePlayer.resetPearlCooldown();
            }
        }
    }
}
