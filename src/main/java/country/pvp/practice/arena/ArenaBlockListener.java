package country.pvp.practice.arena;

import com.google.inject.Inject;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.MatchManager;
import country.pvp.practice.player.PlayerListener;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.util.message.Sender;
import org.bukkit.Difficulty;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class ArenaBlockListener extends PlayerListener {

    private final MatchManager matchManager;

    @Inject
    public ArenaBlockListener(PlayerManager playerManager, MatchManager matchManager) {
        super(playerManager);
        this.matchManager = matchManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void breakBlock(BlockBreakEvent event) {
        PlayerSession playerSession = get(event.getPlayer());

        if (!playerSession.isInMatch()) return;

        Match match = playerSession.getCurrentMatch();
        Block block = event.getBlock();

        if (!match.hasBeenPlacedByPlayer(block)) {
            Sender.messageError(playerSession, "You can only destroy blocks placed by a player.");
            event.setCancelled(true);
            return;
        }

        match.removePlacedBlock(block);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void placeBlock(BlockPlaceEvent event) {
        PlayerSession playerSession = get(event.getPlayer());

        if (!playerSession.isInMatch()) return;

        Match match = playerSession.getCurrentMatch();
        match.addPlacedBlock(event.getBlockPlaced());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void placeBlock(PlayerDropItemEvent event) {
        PlayerSession playerSession = get(event.getPlayer());

        if (!playerSession.isInMatch()) return;

        Match match = playerSession.getCurrentMatch();
        match.addDroppedItem(event.getItemDrop());
    }

    @EventHandler
    public void placeBlock(PlayerDeathEvent event) {
        PlayerSession playerSession = get(event.getEntity());

        if (!playerSession.isInMatch()) return;

        Match match = playerSession.getCurrentMatch();
        event.getDrops().forEach(it -> match.addDroppedItem(event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), it)));
        event.getDrops().clear();
    }


    @EventHandler
    public void onBlockFrom(BlockFromToEvent event) {
        matchManager.getAll()
                .stream()
                .filter(it -> it.getArena().isIn(event.getBlock().getLocation()))
                .findFirst()
                .ifPresent(it -> it.addPlacedBlock(event.getBlock()));
    }

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        event.getWorld().getEntities().clear();
        event.getWorld().setDifficulty(Difficulty.HARD);
    }

    @EventHandler
    public void weatherEvent(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void spawnEvent(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void igniteEvent(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING)
            event.setCancelled(true);
    }

    @EventHandler
    public void decayEvent(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void hangingEvent(HangingBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void burnEvent(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void spreadEvent(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void primeEvent(ExplosionPrimeEvent event) {
        event.setCancelled(true);
    }
}
