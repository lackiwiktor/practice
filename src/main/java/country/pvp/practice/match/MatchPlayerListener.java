package country.pvp.practice.match;

import com.google.inject.Inject;
import country.pvp.practice.player.PlayerListener;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.player.data.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

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

        MatchData matchData = damagedPlayer.getStateData(PlayerState.IN_MATCH);
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

        MatchData matchData = damagedPlayer.getStateData(PlayerState.IN_MATCH);
        Match match = matchData.getMatch();

        if (match.getState() != MatchState.FIGHT) {
            event.setCancelled(true);
            return;
        }

        if (!(event.getDamager() instanceof Player)) return;
        PracticePlayer damagerPlayer = get((Player) event.getDamager());
        if (!damagerPlayer.isInMatch()) return;

        if (match.getTeam(damagedPlayer).hasPlayer(damagerPlayer)) {
            event.setCancelled(true);
            return;
        }

        matchData.setLastAttacker(damagerPlayer);
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        PracticePlayer player = get(event.getEntity());
        if (!player.isInMatch()) return;

        MatchData matchData = player.getStateData(PlayerState.IN_MATCH);
        Match match = matchData.getMatch();
        match.handleDeath(player);
    }

    @EventHandler
    public void playerRespawn(PlayerRespawnEvent event) {
        PracticePlayer player = get(event);
        if (!player.isInMatch()) return;

        event.setRespawnLocation(event.getPlayer().getLocation());

        MatchData matchData = player.getStateData(PlayerState.IN_MATCH);
        Match match = matchData.getMatch();
        match.handleRespawn(player);
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        PracticePlayer player = get(event);
        if (!player.isInMatch()) return;

        MatchData matchData = player.getStateData(PlayerState.IN_MATCH);
        Match match = matchData.getMatch();
        match.handleDisconnect(player);
    }
}
