package country.pvp.practice.match;

import com.google.inject.Inject;
import country.pvp.practice.player.PlayerListener;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PracticePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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

        PlayerMatchData matchData = damagedPlayer.getStateData();
        Match match = matchData.getMatch();

        if (match.getState() != MatchState.FIGHT) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void entityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        PracticePlayer damagedPlayer = get((Player) event.getEntity());
        if (!damagedPlayer.isInMatch()) return;

        PlayerMatchData matchData = damagedPlayer.getStateData();
        Match match = matchData.getMatch();

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

        PlayerMatchData damagerMatchData = damagerPlayer.getStateData();

        damagerMatchData.handleHit();
        matchData.handleBeingHit(damagerPlayer);
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        PracticePlayer player = get(event.getEntity());
        if (!player.isInMatch()) return;

        PlayerMatchData matchData = player.getStateData();
        Match match = matchData.getMatch();
        match.handleDeath(player);
    }

    /*
    @EventHandler
    public void playerRespawn(PlayerRespawnEvent event) {
        System.out.println("NOT HANDLED??");

        PracticePlayer player = get(event);
        if (!player.isInMatch()) return;

        event.setRespawnLocation(event.getPlayer().getLocation());

        PlayerMatchData matchData = player.getStateData();
        Match match = matchData.getMatch();
        match.handleRespawn(player);

        System.out.println("NOT HANDLED??2");
    }
     */

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void breakEvent(BlockBreakEvent event) {
        PracticePlayer practicePlayer = get(event.getPlayer());

        if(practicePlayer.isInMatch()) {
            PlayerMatchData matchData = practicePlayer.getStateData();
            Match match = matchData.getMatch();

            event.setCancelled(match.getState() != MatchState.FIGHT || !match.isBuild());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void placeBlock(BlockPlaceEvent event) {
        PracticePlayer practicePlayer = get(event.getPlayer());

        if(practicePlayer.isInMatch()) {
            PlayerMatchData matchData = practicePlayer.getStateData();
            Match match = matchData.getMatch();

            event.setCancelled(match.getState() != MatchState.FIGHT || !match.isBuild());
        }
    }


    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        PracticePlayer player = get(event);
        if (!player.isInMatch()) return;

        PlayerMatchData matchData = player.getStateData();
        Match match = matchData.getMatch();
        match.handleDisconnect(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPotionSplashEvent(PotionSplashEvent event) {
        if (event.getPotion().getShooter() instanceof Player) {
            Player shooter = (Player) event.getPotion().getShooter();
            PracticePlayer player = playerManager.get(shooter);

            if (!player.isInMatch()) return;

            PlayerMatchData matchData = player.getStateData();
            matchData.increaseThrownPotions();

            if (event.getIntensity(shooter) <= 0.5D) {
                matchData.increaseMissedPotions();
            }

            Match match = matchData.getMatch();
            for (LivingEntity entity : event.getAffectedEntities()) {
                if (entity instanceof Player) {
                    PracticePlayer affectedPlayer = playerManager.get((Player) entity);

                    if (!affectedPlayer.isInMatch() || !match.isInMatch(affectedPlayer))
                        event.setIntensity((LivingEntity) entity, 0);
                }
            }
        }
    }
}
